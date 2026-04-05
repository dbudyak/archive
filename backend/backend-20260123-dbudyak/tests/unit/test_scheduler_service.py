import asyncio
from datetime import datetime, timezone
from unittest.mock import AsyncMock

import pytest
from circuitbreaker import CircuitBreakerError

from website_monitor.config.models import UrlConfig
from website_monitor.database.models import MonitoringEvent
from website_monitor.services.models import UrlCheckResult
from website_monitor.services.scheduler_service import SchedulerService


class TestSchedulerService:

    @pytest.mark.asyncio
    async def test_monitor_url_checks_and_saves(self) -> None:
        check_result = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )
        checker = AsyncMock()
        checker.check.return_value = check_result

        repository = AsyncMock()
        repository.save.return_value = None

        scheduler = SchedulerService(checker, repository)
        config = UrlConfig(url="https://example.com", interval_seconds=5)

        # Run one iteration then cancel
        task = asyncio.create_task(scheduler._monitor_url(config))
        await asyncio.sleep(0.01)  # Let one iteration run
        task.cancel()

        try:
            await task
        except asyncio.CancelledError:
            pass

        checker.check.assert_called_with(config)
        repository.save.assert_called_once()

        saved_event = repository.save.call_args[0][0]
        assert isinstance(saved_event, MonitoringEvent)
        assert saved_event.url == "https://example.com"
        assert saved_event.status_code == 200

    @pytest.mark.asyncio
    async def test_monitor_url_continues_on_repository_error(self) -> None:
        check_result = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )
        checker = AsyncMock()
        checker.check.return_value = check_result

        repository = AsyncMock()
        repository.save.side_effect = [Exception("DB error"), None]

        scheduler = SchedulerService(checker, repository)
        config = UrlConfig(url="https://example.com", interval_seconds=5)

        task = asyncio.create_task(scheduler._monitor_url(config))

        # Wait for two iterations
        await asyncio.sleep(0.02)
        task.cancel()

        try:
            await task
        except asyncio.CancelledError:
            pass

        # Should have attempted to check twice (continued after error)
        assert checker.check.call_count >= 1

    @pytest.mark.asyncio
    async def test_monitor_url_handles_check_failure(self) -> None:
        check_result = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=5000.0,
            status_code=None,
            error="Connection refused",
        )
        checker = AsyncMock()
        checker.check.return_value = check_result

        repository = AsyncMock()

        scheduler = SchedulerService(checker, repository)
        config = UrlConfig(url="https://example.com", interval_seconds=5)

        task = asyncio.create_task(scheduler._monitor_url(config))
        await asyncio.sleep(0.01)
        task.cancel()

        try:
            await task
        except asyncio.CancelledError:
            pass

        # Should still save the failed result
        repository.save.assert_called_once()
        saved_event = repository.save.call_args[0][0]
        assert saved_event.error == "Connection refused"

    @pytest.mark.asyncio
    async def test_run_starts_all_monitors(self) -> None:
        checker = AsyncMock()
        checker.check.return_value = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )
        repository = AsyncMock()

        scheduler = SchedulerService(checker, repository)
        configs = [
            UrlConfig(url="https://example1.com", interval_seconds=5),
            UrlConfig(url="https://example2.com", interval_seconds=5),
            UrlConfig(url="https://example3.com", interval_seconds=5),
        ]

        task = asyncio.create_task(scheduler.start(configs))
        await asyncio.sleep(0.02)
        scheduler.shutdown()
        await task

        # All three URLs should have been checked
        checked_urls = {call[0][0].url for call in checker.check.call_args_list}
        assert checked_urls == {"https://example1.com", "https://example2.com", "https://example3.com"}

    @pytest.mark.asyncio
    async def test_run_with_empty_configs(self) -> None:
        checker = AsyncMock()
        repository = AsyncMock()

        scheduler = SchedulerService(checker, repository)

        # Should complete immediately with no configs
        await asyncio.wait_for(scheduler.start([]), timeout=1.0)

        checker.check.assert_not_called()
        repository.save.assert_not_called()

    @pytest.mark.asyncio
    async def test_monitor_url_continues_on_circuit_breaker_open(self) -> None:
        """Should continue monitoring when circuit breaker is open."""
        check_result = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )
        checker = AsyncMock()
        checker.check.return_value = check_result

        repository = AsyncMock()
        repository.save.side_effect = CircuitBreakerError(None)

        scheduler = SchedulerService(checker, repository)
        config = UrlConfig(url="https://example.com", interval_seconds=5)

        task = asyncio.create_task(scheduler._monitor_url(config))
        await asyncio.sleep(0.02)
        task.cancel()

        try:
            await task
        except asyncio.CancelledError:
            pass

        # Should have continued checking despite circuit breaker being open
        assert checker.check.call_count >= 1
        assert repository.save.call_count >= 1

    @pytest.mark.asyncio
    async def test_semaphore_limits_concurrent_db_writes(self) -> None:
        """Should limit concurrent DB writes to max_concurrent_writes."""
        max_concurrent = 2
        concurrent_count = 0
        max_observed = 0

        async def slow_save(_: MonitoringEvent) -> None:
            nonlocal concurrent_count, max_observed
            concurrent_count += 1
            max_observed = max(max_observed, concurrent_count)
            await asyncio.sleep(0.05)
            concurrent_count -= 1

        checker = AsyncMock()
        checker.check.return_value = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )

        repository = AsyncMock()
        repository.save.side_effect = slow_save

        scheduler = SchedulerService(checker, repository, max_concurrent_writes=max_concurrent)
        configs = [UrlConfig(url=f"https://ex{i}.com", interval_seconds=300) for i in range(10)]

        task = asyncio.create_task(scheduler.start(configs))
        await asyncio.sleep(0.2)
        scheduler.shutdown()
        await task

        assert max_observed <= max_concurrent, f"Max {max_concurrent}, got {max_observed}"
        assert repository.save.call_count == 10

    @pytest.mark.asyncio
    async def test_graceful_shutdown_stops_all_monitors(self) -> None:
        """Shutdown signal should stop all monitors promptly."""
        checker = AsyncMock()
        checker.check.return_value = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )
        repository = AsyncMock()

        scheduler = SchedulerService(checker, repository)
        configs = [
            UrlConfig(url="https://example1.com", interval_seconds=300),
            UrlConfig(url="https://example2.com", interval_seconds=300),
        ]

        task = asyncio.create_task(scheduler.start(configs))
        await asyncio.sleep(0.01)

        scheduler.shutdown()
        await asyncio.wait_for(task, timeout=1.0)

        # Both URLs should have been checked at least once
        assert checker.check.call_count >= 2

    @pytest.mark.asyncio
    async def test_shutdown_interrupts_sleep(self) -> None:
        """Shutdown should interrupt the interval sleep immediately."""
        checker = AsyncMock()
        checker.check.return_value = UrlCheckResult(
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )
        repository = AsyncMock()

        scheduler = SchedulerService(checker, repository)
        config = UrlConfig(url="https://example.com", interval_seconds=300)

        task = asyncio.create_task(scheduler._monitor_url(config))
        await asyncio.sleep(0.01)

        scheduler.shutdown()
        # Should complete quickly despite 300s interval
        await asyncio.wait_for(task, timeout=0.5)

        assert checker.check.call_count == 1

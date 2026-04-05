from datetime import datetime, timezone
from unittest.mock import AsyncMock, MagicMock

import pytest
from circuitbreaker import CircuitBreakerError, CircuitBreakerMonitor

from website_monitor.database.models import MonitoringEvent
from website_monitor.database.repository import MonitoringEventsRepository


class TestRepositoryCircuitBreaker:
    """Integration tests for circuit breaker on repository."""

    @pytest.fixture(autouse=True)
    def reset_circuit_breaker(self) -> None:
        """Reset circuit breaker state before each test."""
        for breaker in CircuitBreakerMonitor.get_circuits():
            breaker._state = "closed"
            breaker._failure_count = 0

    @pytest.fixture
    def mock_pool(self) -> MagicMock:
        pool = MagicMock()
        conn = AsyncMock()
        pool.connection.return_value.__aenter__ = AsyncMock(return_value=conn)
        pool.connection.return_value.__aexit__ = AsyncMock(return_value=None)
        return pool

    @pytest.fixture
    def sample_event(self) -> MonitoringEvent:
        return MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
            regex_matched=None,
            error=None,
        )

    @pytest.mark.asyncio
    async def test_circuit_opens_after_failures(
        self, mock_pool: MagicMock, sample_event: MonitoringEvent
    ) -> None:
        """Circuit should open after 5 consecutive failures."""
        conn = AsyncMock()
        conn.execute.side_effect = Exception("DB connection failed")
        mock_pool.connection.return_value.__aenter__ = AsyncMock(return_value=conn)

        repo = MonitoringEventsRepository(mock_pool)

        # Trigger 5 failures to open circuit
        for _ in range(5):
            with pytest.raises(Exception, match="DB connection failed"):
                await repo.save(sample_event)

        # 6th call should raise CircuitBreakerError (circuit is open)
        with pytest.raises(CircuitBreakerError):
            await repo.save(sample_event)

    @pytest.mark.asyncio
    async def test_circuit_stays_closed_on_success(
        self, mock_pool: MagicMock, sample_event: MonitoringEvent
    ) -> None:
        """Circuit should stay closed when operations succeed."""
        conn = AsyncMock()
        conn.execute.return_value = None
        mock_pool.connection.return_value.__aenter__ = AsyncMock(return_value=conn)

        repo = MonitoringEventsRepository(mock_pool)

        # Multiple successful saves should not trigger circuit breaker
        for _ in range(10):
            await repo.save(sample_event)

        # Should still work
        await repo.save(sample_event)
        assert conn.execute.call_count == 11

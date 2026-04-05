import asyncio
import logging
from contextlib import suppress

from circuitbreaker import CircuitBreakerError

from website_monitor.config.models import UrlConfig
from website_monitor.database.errors import DatabaseError
from website_monitor.database.repository import MonitoringEventsRepository
from website_monitor.services.mappers import result_mapper
from website_monitor.services.url_check_service import UrlCheckService

logger = logging.getLogger(__name__)


class SchedulerService:

    def __init__(
        self,
        url_checker: UrlCheckService,
        repository: MonitoringEventsRepository,
        max_concurrent_writes: int = 15,
    ):
        self._url_checker = url_checker
        self._repository = repository
        self._shutdown_event = asyncio.Event()
        self._db_semaphore = asyncio.Semaphore(max_concurrent_writes)

    def shutdown(self) -> None:
        logger.info("Shutdown signal received")
        self._shutdown_event.set()

    async def start(self, configs: list[UrlConfig]) -> None:
        logger.info("Starting scheduler with %d URLs", len(configs))
        tasks = [asyncio.create_task(self._monitor_url(config)) for config in configs]
        await asyncio.gather(*tasks, return_exceptions=True)
        logger.info("All %d monitors stopped", len(tasks))

    async def _monitor_url(self, config: UrlConfig) -> None:
        logger.info("Starting monitor for %s (interval: %ds)", config.url, config.interval_seconds)

        while not self._shutdown_event.is_set():
            result = await self._url_checker.check(config)
            try:
                async with self._db_semaphore:
                    await self._repository.save(result_mapper(config.url, result))
                logger.debug(
                    "Saved %s: status=%s, time=%.0fms",
                    config.url,
                    result.status_code,
                    result.response_time_ms,
                )
            except CircuitBreakerError:
                logger.warning("Database circuit breaker open, skipping save for %s", config.url)
            except DatabaseError as e:
                logger.error("Database error for %s: %s", config.url, e)
            except asyncio.CancelledError:
                logger.debug("Monitor task cancelled for %s", config.url)
                raise
            except Exception as e:
                logger.exception("Unexpected error saving result for %s: %s", config.url, e)

            # Interruptible sleep - exits immediately on shutdown signal
            with suppress(asyncio.TimeoutError):
                await asyncio.wait_for(self._shutdown_event.wait(), timeout=config.interval_seconds)

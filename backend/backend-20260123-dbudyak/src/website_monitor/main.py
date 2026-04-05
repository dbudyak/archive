import asyncio
import logging
import signal
import sys
from typing import NoReturn

import aiohttp
import psycopg
from psycopg_pool import AsyncConnectionPool

from website_monitor.config.config_loader import load_config
from website_monitor.database.migrations import run_migrations
from website_monitor.database.repository import MonitoringEventsRepository
from website_monitor.net.http_client import HttpClient
from website_monitor.services.scheduler_service import SchedulerService
from website_monitor.services.url_check_service import UrlCheckService

logger = logging.getLogger(__name__)


def _exit_on_db_error(error: Exception) -> NoReturn:
    logger.error("Database connection failed: %s", error)
    logger.error("Check DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD in .env")
    sys.exit(1)


async def run() -> None:
    settings = load_config()

    logging.basicConfig(
        level=getattr(logging, settings.log_level.upper()),
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    )

    try:
        run_migrations(settings.database_url)
    except Exception as e:
        _exit_on_db_error(e)
    logger.info("Database migrations completed")

    try:
        pool = AsyncConnectionPool(
            settings.database_url,
            min_size=settings.db_pool_min_size,
            max_size=settings.db_pool_max_size,
            timeout=settings.db_pool_timeout,
        )
        await pool.open()
        await pool.wait()
    except (psycopg.OperationalError, OSError) as e:
        _exit_on_db_error(e)

    logger.info("Connection pool ready (min=%d, max=%d)", pool.min_size, pool.max_size)

    async with pool, aiohttp.ClientSession() as session:
        scheduler = SchedulerService(
            url_checker=UrlCheckService(
                HttpClient(session),
                retry_count=settings.http_retry_count,
                retry_delay=settings.http_retry_delay,
            ),
            repository=MonitoringEventsRepository(pool, settings.db_operation_timeout),
            max_concurrent_writes=settings.db_max_concurrent_writes,
        )

        for sig in (signal.SIGTERM, signal.SIGINT):
            asyncio.get_running_loop().add_signal_handler(sig, scheduler.shutdown)

        await scheduler.start(settings.url_configs)


def main() -> None:
    asyncio.run(run())


if __name__ == "__main__":
    main()

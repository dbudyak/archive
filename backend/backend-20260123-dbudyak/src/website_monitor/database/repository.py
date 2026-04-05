import asyncio
import logging

import psycopg
from circuitbreaker import circuit
from psycopg_pool import AsyncConnectionPool

from website_monitor.database.errors import DatabaseError
from website_monitor.database.models import MonitoringEvent

logger = logging.getLogger(__name__)


class MonitoringEventsRepository:

    def __init__(
        self,
        pool: AsyncConnectionPool,
        operation_timeout: float = 10.0,
    ):
        self._pool = pool
        self._operation_timeout = operation_timeout

    @circuit(failure_threshold=5, recovery_timeout=30)
    async def save(self, result: MonitoringEvent) -> None:
        try:
            async with asyncio.timeout(self._operation_timeout):
                async with self._pool.connection() as conn:
                    await conn.execute(
                        """INSERT INTO monitoring_events
                        (url, check_time, response_time, status_code, regex_match, error)
                        VALUES (%s, %s, %s, %s, %s, %s)""",
                        (
                            result.url,
                            result.timestamp,
                            result.response_time_ms,
                            result.status_code,
                            result.regex_matched,
                            result.error,
                        ),
                    )
        except TimeoutError as e:
            raise DatabaseError(f"Database operation timed out: {e}", original=e) from e
        except psycopg.OperationalError as e:
            raise DatabaseError(f"Database connection error: {e}", original=e) from e
        except psycopg.Error as e:
            raise DatabaseError(f"Database error: {e}", original=e) from e


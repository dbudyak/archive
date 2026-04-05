from datetime import datetime, timezone

import psycopg
import pytest
from psycopg_pool import AsyncConnectionPool

from website_monitor.database.errors import DatabaseError
from website_monitor.database.models import MonitoringEvent
from website_monitor.database.repository import MonitoringEventsRepository


class TestMonitoringEventsRepository:

    @pytest.mark.asyncio
    async def test_save_inserts_event(
        self, db_conninfo: str, db_pool: AsyncConnectionPool, clean_db: None
    ) -> None:
        repository = MonitoringEventsRepository(db_pool)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=150.5,
            status_code=200,
            regex_matched=True,
            error=None,
        )

        await repository.save(event)

        # Verify data was inserted
        async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
            result = await conn.execute(
                "SELECT url, status_code, regex_match FROM monitoring_events"
            )
            row = await result.fetchone()
            assert row is not None
            assert row[0] == "https://example.com"
            assert row[1] == 200
            assert row[2] is True

    @pytest.mark.asyncio
    async def test_save_handles_null_fields(
        self, db_conninfo: str, db_pool: AsyncConnectionPool, clean_db: None
    ) -> None:
        repository = MonitoringEventsRepository(db_pool)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=5000.0,
            status_code=None,
            regex_matched=None,
            error=None,
        )

        await repository.save(event)

        async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
            result = await conn.execute("SELECT status_code, regex_match FROM monitoring_events")
            row = await result.fetchone()
            assert row is not None
            assert row[0] is None
            assert row[1] is None

    @pytest.mark.asyncio
    async def test_save_stores_error_message(
        self, db_conninfo: str, db_pool: AsyncConnectionPool, clean_db: None
    ) -> None:
        """Verify that HTTP errors are stored in the database."""
        repository = MonitoringEventsRepository(db_pool)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=5000.0,
            status_code=None,
            regex_matched=None,
            error="Connection refused",
        )

        await repository.save(event)

        async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
            result = await conn.execute("SELECT error FROM monitoring_events")
            row = await result.fetchone()
            assert row is not None
            assert row[0] == "Connection refused"

    @pytest.mark.asyncio
    async def test_save_multiple_events(
        self, db_conninfo: str, db_pool: AsyncConnectionPool, clean_db: None
    ) -> None:
        repository = MonitoringEventsRepository(db_pool)

        for i in range(3):
            event = MonitoringEvent(
                url=f"https://example{i}.com",
                timestamp=datetime.now(timezone.utc),
                response_time_ms=100.0 + i,
                status_code=200,
            )
            await repository.save(event)

        async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
            result = await conn.execute("SELECT COUNT(*) FROM monitoring_events")
            row = await result.fetchone()
            assert row is not None
            assert row[0] == 3

    @pytest.mark.asyncio
    async def test_save_preserves_timestamp(
        self, db_conninfo: str, db_pool: AsyncConnectionPool, clean_db: None
    ) -> None:
        repository = MonitoringEventsRepository(db_pool)

        timestamp = datetime(2024, 1, 15, 10, 30, 0, tzinfo=timezone.utc)
        event = MonitoringEvent(
            url="https://example.com",
            timestamp=timestamp,
            response_time_ms=100.0,
            status_code=200,
        )

        await repository.save(event)

        async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
            result = await conn.execute("SELECT check_time FROM monitoring_events")
            row = await result.fetchone()
            assert row is not None
            assert row[0] == timestamp

    @pytest.mark.asyncio
    async def test_save_times_out_on_slow_database(
        self, db_conninfo: str, db_pool: AsyncConnectionPool, clean_db: None
    ) -> None:
        """Verify that database operations timeout if database is slow."""
        # Create repository with very short timeout (0.5 seconds)
        repository = MonitoringEventsRepository(db_pool, operation_timeout=0.5)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
        )

        # Lock the table exclusively from another connection to block INSERTs
        blocking_conn = await psycopg.AsyncConnection.connect(db_conninfo)
        try:
            await blocking_conn.execute("BEGIN")
            await blocking_conn.execute("LOCK TABLE monitoring_events IN EXCLUSIVE MODE")

            with pytest.raises(DatabaseError) as exc_info:
                await repository.save(event)
            assert "timed out" in str(exc_info.value).lower()
        finally:
            await blocking_conn.execute("ROLLBACK")
            await blocking_conn.close()

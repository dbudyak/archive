from datetime import datetime, timezone
from unittest.mock import AsyncMock

import psycopg
import pytest
from psycopg_pool import AsyncConnectionPool

from website_monitor.database.errors import DatabaseError
from website_monitor.database.models import MonitoringEvent
from website_monitor.database.repository import MonitoringEventsRepository


class TestRepositorySaveQuery:
    """Unit tests for repository save method - no database required."""

    @pytest.mark.asyncio
    async def test_save_includes_error_in_insert(self) -> None:
        """Verify that save() includes error column in INSERT statement."""
        mock_conn = AsyncMock()
        mock_conn.__aenter__ = AsyncMock(return_value=mock_conn)
        mock_conn.__aexit__ = AsyncMock(return_value=None)

        mock_pool = AsyncMock(spec=AsyncConnectionPool)
        mock_pool.connection.return_value = mock_conn

        repository = MonitoringEventsRepository(mock_pool)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=5000.0,
            status_code=None,
            regex_matched=None,
            error="Connection refused",
        )

        await repository.save(event)

        # Verify execute was called
        mock_conn.execute.assert_called_once()
        call_args = mock_conn.execute.call_args

        # Check SQL contains error column
        sql = call_args[0][0]
        params = call_args[0][1]

        assert "error" in sql, f"SQL should include 'error' column: {sql}"
        assert "Connection refused" in params, f"Params should include error: {params}"

    @pytest.mark.asyncio
    async def test_save_wraps_operational_error_in_database_error(self) -> None:
        """Verify that psycopg.OperationalError is wrapped in DatabaseError."""
        mock_conn = AsyncMock()
        mock_conn.__aenter__ = AsyncMock(return_value=mock_conn)
        mock_conn.__aexit__ = AsyncMock(return_value=None)
        mock_conn.execute.side_effect = psycopg.OperationalError("connection refused")

        mock_pool = AsyncMock(spec=AsyncConnectionPool)
        mock_pool.connection.return_value = mock_conn

        repository = MonitoringEventsRepository(mock_pool)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
            regex_matched=None,
            error=None,
        )

        with pytest.raises(DatabaseError) as exc_info:
            await repository.save(event)

        assert "connection" in str(exc_info.value).lower()
        assert exc_info.value.retryable is True

    @pytest.mark.asyncio
    async def test_save_wraps_timeout_error_in_database_error(self) -> None:
        """Verify that TimeoutError is wrapped in DatabaseError."""
        mock_conn = AsyncMock()
        mock_conn.__aenter__ = AsyncMock(return_value=mock_conn)
        mock_conn.__aexit__ = AsyncMock(return_value=None)
        mock_conn.execute.side_effect = TimeoutError("operation timed out")

        mock_pool = AsyncMock(spec=AsyncConnectionPool)
        mock_pool.connection.return_value = mock_conn

        repository = MonitoringEventsRepository(mock_pool)

        event = MonitoringEvent(
            url="https://example.com",
            timestamp=datetime.now(timezone.utc),
            response_time_ms=100.0,
            status_code=200,
            regex_matched=None,
            error=None,
        )

        with pytest.raises(DatabaseError) as exc_info:
            await repository.save(event)

        assert "timed out" in str(exc_info.value).lower()
        assert exc_info.value.retryable is True

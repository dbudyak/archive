import os
from collections.abc import AsyncGenerator

import psycopg
import pytest
import pytest_asyncio
from psycopg_pool import AsyncConnectionPool

from website_monitor.database.migrations import run_migrations


def get_test_db_conninfo() -> str:
    """Get test database connection info from environment."""
    host = os.environ.get("TEST_DB_HOST", "localhost")
    port = os.environ.get("TEST_DB_PORT", "5432")
    name = os.environ.get("TEST_DB_NAME", "website_monitor_test")
    user = os.environ.get("TEST_DB_USER", "postgres")
    password = os.environ.get("TEST_DB_PASSWORD", "postgres")

    return f"host={host} port={port} dbname={name} user={user} password={password}"


def is_postgres_available() -> bool:
    """Check if PostgreSQL is available for integration tests."""
    conninfo = get_test_db_conninfo()

    try:
        with psycopg.connect(conninfo, connect_timeout=2) as conn:
            conn.execute("SELECT 1")
        return True
    except Exception:
        return False


# Skip all tests in this directory if PostgreSQL is not available
pytestmark = pytest.mark.skipif(
    not is_postgres_available(),
    reason="PostgreSQL not available for integration tests"
)


@pytest.fixture
def db_conninfo() -> str:
    """Provide database connection info for tests."""
    return get_test_db_conninfo()


@pytest_asyncio.fixture
async def db_pool(db_conninfo: str) -> AsyncGenerator[AsyncConnectionPool, None]:
    """Provide a connection pool for tests."""
    async with AsyncConnectionPool(db_conninfo) as pool:
        yield pool


@pytest_asyncio.fixture
async def clean_db(db_conninfo: str) -> AsyncGenerator[None, None]:
    """Clean up test tables and run migrations before each test."""
    async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
        await conn.execute("DROP TABLE IF EXISTS monitoring_events CASCADE")
        await conn.execute("DROP TABLE IF EXISTS _yoyo_migration CASCADE")
        await conn.execute("DROP TABLE IF EXISTS _yoyo_log CASCADE")
        await conn.execute("DROP TABLE IF EXISTS _yoyo_version CASCADE")
        await conn.commit()

    # Run migrations to create schema
    run_migrations(db_conninfo)

    yield

    async with await psycopg.AsyncConnection.connect(db_conninfo) as conn:
        await conn.execute("DROP TABLE IF EXISTS monitoring_events CASCADE")
        await conn.execute("DROP TABLE IF EXISTS _yoyo_migration CASCADE")
        await conn.execute("DROP TABLE IF EXISTS _yoyo_log CASCADE")
        await conn.execute("DROP TABLE IF EXISTS _yoyo_version CASCADE")
        await conn.commit()

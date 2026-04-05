import logging
from pathlib import Path

from yoyo import get_backend, read_migrations

logger = logging.getLogger(__name__)


MIGRATIONS_DIR = Path("migrations")


def run_migrations(database_url: str) -> None:
    # yoyo needs postgresql:// URL format, not psycopg's key=value format
    yoyo_url = _convert_connection_string(database_url)

    backend = get_backend(yoyo_url)
    migrations = read_migrations(str(MIGRATIONS_DIR))

    with backend.lock():
        pending = backend.to_apply(migrations)
        if pending:
            logger.info("Applying %d pending migration(s)...", len(pending))
            backend.apply_migrations(pending)
            logger.info("Migrations applied successfully")
        else:
            logger.debug("No pending migrations")


def _convert_connection_string(psycopg_url: str) -> str:
    params = {}
    for part in psycopg_url.split():
        if "=" in part:
            key, value = part.split("=", 1)
            params[key] = value

    host = params.get("host", "localhost")
    port = params.get("port", "5432")
    dbname = params.get("dbname", "")
    user = params.get("user", "")
    password = params.get("password", "")

    return f"postgresql://{user}:{password}@{host}:{port}/{dbname}"

import csv
import os
from pathlib import Path

from dotenv import load_dotenv

from website_monitor.config.models import Config, UrlConfig

DEFAULT_URLS_FILE = Path("config/urls.csv")


def _get_required_env(name: str) -> str:
    value = os.environ.get(name)
    if not value:
        raise EnvironmentError(
            f"Missing required environment variable: {name}\n"
            f"Please set {name} or copy .env.example to .env and configure it."
        )
    return value


def load_urls(file_path: Path | None = None) -> list[UrlConfig]:
    path = file_path or Path(os.environ.get("URLS_FILE", DEFAULT_URLS_FILE))

    if not path.exists():
        raise FileNotFoundError(f"URLs config file not found: {path}")

    urls = []
    with open(path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            regex = row.get("regex_pattern", "").strip() or None
            urls.append(
                UrlConfig(
                    url=row["url"].strip(),
                    interval_seconds=int(row["interval_seconds"]),
                    regex_pattern=regex,
                )
            )

    if not urls:
        raise FileNotFoundError(f"No urls found in {file_path}")

    return urls


def load_config() -> Config:
    load_dotenv()
    return Config(
        db_host=_get_required_env("DB_HOST"),
        db_port=int(os.environ.get("DB_PORT", "5432")),
        db_name=_get_required_env("DB_NAME"),
        db_user=_get_required_env("DB_USERNAME"),
        db_password=_get_required_env("DB_PASSWORD"),
        log_level=os.environ.get("LOG_LEVEL", "INFO"),
        url_configs=load_urls(),
        db_pool_min_size=int(os.environ.get("DB_POOL_MIN_SIZE", "4")),
        db_pool_max_size=int(os.environ.get("DB_POOL_MAX_SIZE", "15")),
        db_pool_timeout=float(os.environ.get("DB_POOL_TIMEOUT", "120.0")),
        db_operation_timeout=float(os.environ.get("DB_OPERATION_TIMEOUT", "10.0")),
        db_max_concurrent_writes=int(os.environ.get("DB_MAX_CONCURRENT_WRITES", "15")),
        http_retry_count=int(os.environ.get("HTTP_RETRY_COUNT", "3")),
        http_retry_delay=float(os.environ.get("HTTP_RETRY_DELAY", "1.0")),
    )

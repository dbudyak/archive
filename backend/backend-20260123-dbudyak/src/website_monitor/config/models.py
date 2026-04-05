from dataclasses import dataclass
from urllib.parse import urlparse


@dataclass
class UrlConfig:
    url: str
    interval_seconds: int = 30
    regex_pattern: str | None = None

    def __post_init__(self) -> None:
        parsed = urlparse(self.url)
        if parsed.scheme not in ("http", "https"):
            raise ValueError(f"URL must use http or https scheme: {self.url}")
        if not parsed.netloc:
            raise ValueError(f"URL must have a valid host: {self.url}")

        if not 5 <= self.interval_seconds <= 300:
            raise ValueError("Interval must be between 5 and 300 seconds")


@dataclass
class Config:
    db_host: str
    db_port: int
    db_name: str
    db_user: str
    db_password: str
    log_level: str
    url_configs: list[UrlConfig]
    db_pool_min_size: int = 4
    db_pool_max_size: int = 15
    db_pool_timeout: float = 30.0
    db_operation_timeout: float = 10.0
    db_max_concurrent_writes: int = 15
    http_retry_count: int = 3
    http_retry_delay: float = 1.0

    @property
    def database_url(self) -> str:
        return (
            f"host={self.db_host} port={self.db_port} dbname={self.db_name} "
            f"user={self.db_user} password={self.db_password}"
        )


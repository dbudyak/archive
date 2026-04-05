from dataclasses import dataclass
from datetime import datetime


@dataclass
class UrlCheckResult:
    timestamp: datetime
    response_time_ms: float
    status_code: int | None = None
    regex_matched: bool | None = None
    error: str | None = None

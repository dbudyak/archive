import logging
import re
import time
from datetime import UTC, datetime

from website_monitor.config.models import UrlConfig
from website_monitor.net.http_client import HttpClient
from website_monitor.net.retry import with_retry
from website_monitor.services.models import UrlCheckResult

logger = logging.getLogger(__name__)


class UrlCheckService:
    def __init__(
        self,
        http_client: HttpClient,
        retry_count: int = 3,
        retry_delay: float = 1.0,
    ):
        self._client = http_client
        self._retry_count = retry_count
        self._retry_delay = retry_delay

    async def check(self, config: UrlConfig) -> UrlCheckResult:
        start = time.perf_counter()

        async def on_retry(attempt: int, error: Exception) -> None:
            logger.debug(
                "Attempt %d failed for %s: %s, retrying in %.1fs...",
                attempt,
                config.url,
                error,
                self._retry_delay,
            )

        try:
            needs_body = config.regex_pattern is not None
            response = await with_retry(
                lambda: self._client.get(config.url, read_body=needs_body),
                retry_count=self._retry_count,
                retry_delay=self._retry_delay,
                on_retry=on_retry,
            )
            elapsed_ms = (time.perf_counter() - start) * 1000
            regex_matched = await self.check_pattern_match(config, response.text)

            return UrlCheckResult(
                timestamp=datetime.now(UTC),
                response_time_ms=elapsed_ms,
                status_code=response.status_code,
                regex_matched=regex_matched,
                error=None,
            )
        except Exception as e:
            elapsed_ms = (time.perf_counter() - start) * 1000
            logger.warning(
                "Check failed for %s after %d attempts: %s",
                config.url,
                self._retry_count,
                e,
            )
            return UrlCheckResult(
                timestamp=datetime.now(UTC),
                response_time_ms=elapsed_ms,
                status_code=None,
                regex_matched=None,
                error=str(e),
            )

    @staticmethod
    async def check_pattern_match(config: UrlConfig, response: str) -> bool | None:
        regex_matched: bool | None = None
        if config.regex_pattern:
            regex_matched = bool(re.search(config.regex_pattern, response))
            logger.debug(
                "Regex check for %s: pattern=%s, matched=%s",
                config.url,
                config.regex_pattern,
                regex_matched,
            )
        return regex_matched

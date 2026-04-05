from datetime import datetime, timezone

from website_monitor.services.mappers import result_mapper
from website_monitor.services.models import UrlCheckResult


class TestResultMapper:

    def test_maps_successful_result(self) -> None:
        timestamp = datetime.now(timezone.utc)
        result = UrlCheckResult(
            timestamp=timestamp,
            response_time_ms=150.5,
            status_code=200,
            regex_matched=True,
            error=None,
        )

        event = result_mapper("https://example.com", result)

        assert event.url == "https://example.com"
        assert event.timestamp == timestamp
        assert event.response_time_ms == 150.5
        assert event.status_code == 200
        assert event.regex_matched is True
        assert event.error is None

    def test_maps_failed_result(self) -> None:
        timestamp = datetime.now(timezone.utc)
        result = UrlCheckResult(
            timestamp=timestamp,
            response_time_ms=5000.0,
            status_code=None,
            regex_matched=None,
            error="Connection refused",
        )

        event = result_mapper("https://example.com", result)

        assert event.url == "https://example.com"
        assert event.status_code is None
        assert event.regex_matched is None
        assert event.error == "Connection refused"

from website_monitor.database.models import MonitoringEvent
from website_monitor.services.models import UrlCheckResult


def result_mapper(url: str, result: UrlCheckResult) -> MonitoringEvent:
    return MonitoringEvent(
        url=url,
        timestamp=result.timestamp,
        response_time_ms=result.response_time_ms,
        status_code=result.status_code,
        regex_matched=result.regex_matched,
        error=result.error
    )

import pytest

from website_monitor.config.models import UrlConfig
from website_monitor.net.models import HttpResponse
from website_monitor.services.url_check_service import UrlCheckService


class FakeHttpClient:
    def __init__(self, response: HttpResponse | None = None, error: Exception | None = None):
        self.response = response
        self.error = error
        self.last_url: str | None = None
        self.call_count: int = 0

    async def get(self, url: str, timeout: float = 30.0, read_body: bool = False) -> HttpResponse:
        self.last_url = url
        self.call_count += 1
        if self.error:
            raise self.error
        if self.response:
            return self.response
        raise RuntimeError("FakeHttpClient not configured")


class FakeHttpClientWithRetry:
    """HTTP client that fails N times before succeeding."""

    def __init__(
        self,
        fail_count: int,
        response: HttpResponse,
        error: Exception = ConnectionError("Connection refused"),
    ):
        self.fail_count = fail_count
        self.response = response
        self.error = error
        self.call_count: int = 0

    async def get(self, url: str, timeout: float = 30.0, read_body: bool = False) -> HttpResponse:
        self.call_count += 1
        if self.call_count <= self.fail_count:
            raise self.error
        return self.response


class TestUrlCheckService:

    @pytest.mark.asyncio
    async def test_successful_check(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="OK"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.status_code == 200
        assert result.error is None
        assert result.response_time_ms > 0

    @pytest.mark.asyncio
    async def test_regex_match_found(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="Hello World"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com", regex_pattern="World")

        result = await service.check(config)

        assert result.regex_matched is True

    @pytest.mark.asyncio
    async def test_regex_match_not_found(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="Hello World"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com", regex_pattern="Goodbye")

        result = await service.check(config)

        assert result.regex_matched is False

    @pytest.mark.asyncio
    async def test_no_regex_when_pattern_not_set(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="Hello"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.regex_matched is None

    @pytest.mark.asyncio
    async def test_http_error_captured(self) -> None:
        client = FakeHttpClient(error=ConnectionError("Connection refused"))
        service = UrlCheckService(client, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.status_code is None
        assert result.regex_matched is None
        assert "Connection refused" in result.error

    @pytest.mark.asyncio
    async def test_timeout_error_captured(self) -> None:
        client = FakeHttpClient(error=TimeoutError("Request timed out"))
        service = UrlCheckService(client, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.status_code is None
        assert "timed out" in result.error

    @pytest.mark.asyncio
    async def test_uses_correct_url(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="OK"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://specific-url.example.com/path")

        await service.check(config)

        assert client.last_url == "https://specific-url.example.com/path"

    @pytest.mark.asyncio
    async def test_non_200_status_codes(self) -> None:
        for status_code in [201, 301, 404, 500, 503]:
            client = FakeHttpClient(response=HttpResponse(status_code=status_code, text=""))
            service = UrlCheckService(client)
            config = UrlConfig(url="https://example.com")

            result = await service.check(config)

            assert result.status_code == status_code
            assert result.error is None

    @pytest.mark.asyncio
    async def test_regex_pattern_with_special_characters(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="Price: $99.99"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com", regex_pattern=r"\$\d+\.\d+")

        result = await service.check(config)

        assert result.regex_matched is True

    @pytest.mark.asyncio
    async def test_regex_search_not_match(self) -> None:
        # Verify re.search is used (matches anywhere) not re.match (matches at start)
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="prefix-TARGET-suffix"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com", regex_pattern="TARGET")

        result = await service.check(config)

        assert result.regex_matched is True

    @pytest.mark.asyncio
    async def test_timestamp_is_set(self) -> None:
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="OK"))
        service = UrlCheckService(client)
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.timestamp is not None
        assert result.timestamp.tzinfo is not None  # Should be timezone-aware

    @pytest.mark.asyncio
    async def test_error_still_measures_response_time(self) -> None:
        client = FakeHttpClient(error=ConnectionError("Failed"))
        service = UrlCheckService(client, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.response_time_ms >= 0


class TestUrlCheckServiceRetry:
    """Tests for retry logic."""

    @pytest.mark.asyncio
    async def test_retries_on_error_and_succeeds(self) -> None:
        """Should retry and succeed if error is transient."""
        client = FakeHttpClientWithRetry(
            fail_count=2,
            response=HttpResponse(status_code=200, text="OK"),
        )
        service = UrlCheckService(client, retry_count=3, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.status_code == 200
        assert result.error is None
        assert client.call_count == 3  # 2 failures + 1 success

    @pytest.mark.asyncio
    async def test_fails_after_all_retries_exhausted(self) -> None:
        """Should return error after all retries fail."""
        client = FakeHttpClient(error=ConnectionError("Connection refused"))
        service = UrlCheckService(client, retry_count=3, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.status_code is None
        assert result.error is not None
        assert "Connection refused" in result.error
        assert client.call_count == 3

    @pytest.mark.asyncio
    async def test_no_retry_on_success(self) -> None:
        """Should not retry if first attempt succeeds."""
        client = FakeHttpClient(response=HttpResponse(status_code=200, text="OK"))
        service = UrlCheckService(client, retry_count=3, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.status_code == 200
        assert client.call_count == 1

    @pytest.mark.asyncio
    async def test_retry_count_of_one_means_no_retry(self) -> None:
        """With retry_count=1, should fail immediately on error."""
        client = FakeHttpClient(error=ConnectionError("Failed"))
        service = UrlCheckService(client, retry_count=1, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        result = await service.check(config)

        assert result.error is not None
        assert client.call_count == 1

    @pytest.mark.asyncio
    async def test_default_retry_count_is_three(self) -> None:
        """Default retry count should be 3."""
        client = FakeHttpClient(error=ConnectionError("Failed"))
        service = UrlCheckService(client, retry_delay=0.01)  # type: ignore[arg-type]
        config = UrlConfig(url="https://example.com")

        await service.check(config)

        assert client.call_count == 3

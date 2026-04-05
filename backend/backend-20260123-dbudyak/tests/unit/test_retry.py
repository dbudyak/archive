import pytest

from website_monitor.net.retry import with_retry


class TestWithRetry:

    @pytest.mark.asyncio
    async def test_returns_result_on_success(self) -> None:
        async def fn() -> str:
            return "success"

        result = await with_retry(fn)

        assert result == "success"

    @pytest.mark.asyncio
    async def test_retries_on_failure_then_succeeds(self) -> None:
        call_count = 0

        async def fn() -> str:
            nonlocal call_count
            call_count += 1
            if call_count < 3:
                raise ConnectionError("Failed")
            return "success"

        result = await with_retry(fn, retry_count=3, retry_delay=0.01)

        assert result == "success"
        assert call_count == 3

    @pytest.mark.asyncio
    async def test_raises_after_all_retries_exhausted(self) -> None:
        async def fn() -> str:
            raise ConnectionError("Always fails")

        with pytest.raises(ConnectionError, match="Always fails"):
            await with_retry(fn, retry_count=3, retry_delay=0.01)

    @pytest.mark.asyncio
    async def test_calls_on_retry_callback(self) -> None:
        attempts: list[tuple[int, str]] = []

        async def fn() -> str:
            raise ValueError("Error")

        async def on_retry(attempt: int, error: Exception) -> None:
            attempts.append((attempt, str(error)))

        with pytest.raises(ValueError):
            await with_retry(fn, retry_count=3, retry_delay=0.01, on_retry=on_retry)

        assert attempts == [(1, "Error"), (2, "Error")]

    @pytest.mark.asyncio
    async def test_no_callback_on_last_attempt(self) -> None:
        callback_called = False

        async def fn() -> str:
            raise ValueError("Error")

        async def on_retry(attempt: int, error: Exception) -> None:
            nonlocal callback_called
            callback_called = True

        with pytest.raises(ValueError):
            await with_retry(fn, retry_count=1, retry_delay=0.01, on_retry=on_retry)

        assert not callback_called  # Only 1 attempt, no retry

    @pytest.mark.asyncio
    async def test_default_retry_count_is_three(self) -> None:
        call_count = 0

        async def fn() -> str:
            nonlocal call_count
            call_count += 1
            raise ValueError("Error")

        with pytest.raises(ValueError):
            await with_retry(fn, retry_delay=0.01)

        assert call_count == 3

    @pytest.mark.asyncio
    async def test_preserves_exception_type(self) -> None:
        async def fn() -> str:
            raise TimeoutError("Timed out")

        with pytest.raises(TimeoutError):
            await with_retry(fn, retry_count=2, retry_delay=0.01)

    @pytest.mark.asyncio
    async def test_works_with_lambda(self) -> None:
        call_count = 0

        async def make_request() -> str:
            nonlocal call_count
            call_count += 1
            return "response"

        result = await with_retry(lambda: make_request(), retry_count=3, retry_delay=0.01)

        assert result == "response"
        assert call_count == 1

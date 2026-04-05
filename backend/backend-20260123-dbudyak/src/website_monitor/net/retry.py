import asyncio
from collections.abc import Awaitable, Callable
from typing import TypeVar

T = TypeVar("T")


async def with_retry(
    fn: Callable[[], Awaitable[T]],
    retry_count: int = 3,
    retry_delay: float = 1.0,
    on_retry: Callable[[int, Exception], Awaitable[None]] | None = None,
) -> T:
    """Execute fn() with retries. Raises last exception if all attempts fail."""
    last_error: Exception | None = None
    for attempt in range(retry_count):
        try:
            return await fn()
        except Exception as e:
            last_error = e
            if attempt < retry_count - 1:
                if on_retry:
                    await on_retry(attempt + 1, e)
                await asyncio.sleep(retry_delay)
    raise last_error

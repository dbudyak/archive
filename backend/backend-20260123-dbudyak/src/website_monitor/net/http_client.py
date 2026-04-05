import aiohttp

from website_monitor.net.models import HttpResponse

MAX_BODY_SIZE = 1024 * 1024  # 1MB - prevent memory exhaustion


class HttpClient:
    def __init__(self, session: aiohttp.ClientSession):
        self._session = session

    async def get(
        self,
        url: str,
        timeout: float = 30.0,
        read_body: bool = False,
    ) -> HttpResponse:
        async with self._session.get(url, timeout=aiohttp.ClientTimeout(total=timeout)) as response:
            text = ""
            if read_body:
                raw = await response.content.read(MAX_BODY_SIZE)
                text = raw.decode("utf-8", errors="replace")
            return HttpResponse(status_code=response.status, text=text)

from unittest.mock import AsyncMock, MagicMock

import pytest

from website_monitor.net.http_client import MAX_BODY_SIZE, HttpClient


class TestHttpClient:

    @pytest.mark.asyncio
    async def test_get_returns_status_code(self) -> None:
        mock_response = AsyncMock()
        mock_response.status = 200
        mock_response.__aenter__ = AsyncMock(return_value=mock_response)
        mock_response.__aexit__ = AsyncMock(return_value=None)

        mock_session = MagicMock()
        mock_session.get.return_value = mock_response

        client = HttpClient(mock_session)
        response = await client.get("https://example.com")

        assert response.status_code == 200
        assert response.text == ""

    @pytest.mark.asyncio
    async def test_get_reads_body_when_requested(self) -> None:
        mock_response = AsyncMock()
        mock_response.status = 200
        mock_response.content.read = AsyncMock(return_value=b"Hello World")
        mock_response.__aenter__ = AsyncMock(return_value=mock_response)
        mock_response.__aexit__ = AsyncMock(return_value=None)

        mock_session = MagicMock()
        mock_session.get.return_value = mock_response

        client = HttpClient(mock_session)
        response = await client.get("https://example.com", read_body=True)

        assert response.text == "Hello World"
        mock_response.content.read.assert_called_once_with(MAX_BODY_SIZE)

    @pytest.mark.asyncio
    async def test_get_handles_invalid_utf8(self) -> None:
        """Should replace invalid UTF-8 bytes instead of crashing."""
        mock_response = AsyncMock()
        mock_response.status = 200
        mock_response.content.read = AsyncMock(return_value=b"Hello \xff\xfe World")
        mock_response.__aenter__ = AsyncMock(return_value=mock_response)
        mock_response.__aexit__ = AsyncMock(return_value=None)

        mock_session = MagicMock()
        mock_session.get.return_value = mock_response

        client = HttpClient(mock_session)
        response = await client.get("https://example.com", read_body=True)

        assert "Hello" in response.text
        assert "World" in response.text

    @pytest.mark.asyncio
    async def test_get_truncates_large_body(self) -> None:
        """Should only read up to MAX_BODY_SIZE bytes."""
        mock_response = AsyncMock()
        mock_response.status = 200
        mock_response.content.read = AsyncMock(return_value=b"x" * MAX_BODY_SIZE)
        mock_response.__aenter__ = AsyncMock(return_value=mock_response)
        mock_response.__aexit__ = AsyncMock(return_value=None)

        mock_session = MagicMock()
        mock_session.get.return_value = mock_response

        client = HttpClient(mock_session)
        response = await client.get("https://example.com", read_body=True)

        assert len(response.text) == MAX_BODY_SIZE
        mock_response.content.read.assert_called_once_with(MAX_BODY_SIZE)

    @pytest.mark.asyncio
    async def test_get_does_not_read_body_by_default(self) -> None:
        mock_response = AsyncMock()
        mock_response.status = 404
        mock_response.__aenter__ = AsyncMock(return_value=mock_response)
        mock_response.__aexit__ = AsyncMock(return_value=None)

        mock_session = MagicMock()
        mock_session.get.return_value = mock_response

        client = HttpClient(mock_session)
        response = await client.get("https://example.com")

        assert response.status_code == 404
        assert response.text == ""
        mock_response.content.read.assert_not_called()

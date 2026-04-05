import pytest

from website_monitor.config.config_loader import _get_required_env


class TestGetRequiredEnv:

    def test_returns_value_when_set(self, monkeypatch: pytest.MonkeyPatch) -> None:
        monkeypatch.setenv("TEST_VAR", "test_value")
        assert _get_required_env("TEST_VAR") == "test_value"

    def test_raises_when_missing(self, monkeypatch: pytest.MonkeyPatch) -> None:
        monkeypatch.delenv("MISSING_VAR", raising=False)
        with pytest.raises(EnvironmentError) as exc_info:
            _get_required_env("MISSING_VAR")
        assert "MISSING_VAR" in str(exc_info.value)

    def test_raises_when_empty(self, monkeypatch: pytest.MonkeyPatch) -> None:
        monkeypatch.setenv("EMPTY_VAR", "")
        with pytest.raises(EnvironmentError) as exc_info:
            _get_required_env("EMPTY_VAR")
        assert "EMPTY_VAR" in str(exc_info.value)

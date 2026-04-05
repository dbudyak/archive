import pytest

from website_monitor.config.models import UrlConfig


class TestUrlConfigValidation:

    def test_valid_minimum_interval(self) -> None:
        config = UrlConfig(url="https://example.com", interval_seconds=5)
        assert config.interval_seconds == 5

    def test_valid_maximum_interval(self) -> None:
        config = UrlConfig(url="https://example.com", interval_seconds=300)
        assert config.interval_seconds == 300

    def test_invalid_interval_too_low(self) -> None:
        with pytest.raises(ValueError, match="between 5 and 300"):
            UrlConfig(url="https://example.com", interval_seconds=4)

    def test_invalid_interval_too_high(self) -> None:
        with pytest.raises(ValueError, match="between 5 and 300"):
            UrlConfig(url="https://example.com", interval_seconds=301)

    def test_default_interval(self) -> None:
        config = UrlConfig(url="https://example.com")
        assert config.interval_seconds == 30

    def test_regex_pattern_optional(self) -> None:
        config = UrlConfig(url="https://example.com")
        assert config.regex_pattern is None

    def test_regex_pattern_set(self) -> None:
        config = UrlConfig(url="https://example.com", regex_pattern="OK")
        assert config.regex_pattern == "OK"

    def test_valid_https_url(self) -> None:
        config = UrlConfig(url="https://example.com")
        assert config.url == "https://example.com"

    def test_valid_http_url(self) -> None:
        config = UrlConfig(url="http://example.com")
        assert config.url == "http://example.com"

    def test_invalid_scheme_ftp(self) -> None:
        with pytest.raises(ValueError, match="http or https"):
            UrlConfig(url="ftp://example.com")

    def test_invalid_scheme_file(self) -> None:
        with pytest.raises(ValueError, match="http or https"):
            UrlConfig(url="file:///etc/passwd")

    def test_invalid_scheme_javascript(self) -> None:
        with pytest.raises(ValueError, match="http or https"):
            UrlConfig(url="javascript:alert(1)")

    def test_invalid_no_host(self) -> None:
        with pytest.raises(ValueError, match="valid host"):
            UrlConfig(url="https://")

    def test_invalid_no_scheme(self) -> None:
        with pytest.raises(ValueError, match="http or https"):
            UrlConfig(url="example.com")

    def test_url_with_port(self) -> None:
        config = UrlConfig(url="https://example.com:8443/path")
        assert config.url == "https://example.com:8443/path"

    def test_url_with_query_params(self) -> None:
        config = UrlConfig(url="https://example.com/search?q=test&page=1")
        assert config.url == "https://example.com/search?q=test&page=1"

    def test_url_with_unicode(self) -> None:
        config = UrlConfig(url="https://例え.jp/パス")
        assert "例え" in config.url

    def test_url_with_encoded_chars(self) -> None:
        config = UrlConfig(url="https://example.com/path%20with%20spaces")
        assert config.url == "https://example.com/path%20with%20spaces"

    def test_interval_boundary_values(self) -> None:
        """Test exact boundary values 5 and 300."""
        config_min = UrlConfig(url="https://example.com", interval_seconds=5)
        config_max = UrlConfig(url="https://example.com", interval_seconds=300)
        assert config_min.interval_seconds == 5
        assert config_max.interval_seconds == 300

    def test_regex_pattern_with_groups(self) -> None:
        config = UrlConfig(url="https://example.com", regex_pattern=r"(\d+)-(\w+)")
        assert config.regex_pattern == r"(\d+)-(\w+)"

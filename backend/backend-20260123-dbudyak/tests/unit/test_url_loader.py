from pathlib import Path

import pytest

from website_monitor.config.config_loader import load_urls


class TestLoadUrls:

    def test_loads_urls_from_csv(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,interval_seconds,regex_pattern\n"
            "https://example.com,30,Example\n"
            "https://test.com,60,\n"
        )

        urls = load_urls(csv_file)

        assert len(urls) == 2
        assert urls[0].url == "https://example.com"
        assert urls[0].interval_seconds == 30
        assert urls[0].regex_pattern == "Example"

    def test_handles_empty_regex(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,interval_seconds,regex_pattern\n"
            "https://example.com,30,\n"
        )

        urls = load_urls(csv_file)

        assert urls[0].regex_pattern is None

    def test_handles_whitespace_in_values(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,interval_seconds,regex_pattern\n"
            "  https://example.com  ,30,  pattern  \n"
        )

        urls = load_urls(csv_file)

        assert urls[0].url == "https://example.com"
        assert urls[0].regex_pattern == "pattern"

    def test_raises_on_missing_file(self) -> None:
        with pytest.raises(FileNotFoundError):
            load_urls(Path("/nonexistent/urls.csv"))

    def test_loads_default_urls_file(self) -> None:
        urls = load_urls()

        assert len(urls) > 0
        assert all(url.url.startswith("https://") for url in urls)
        assert all(5 <= url.interval_seconds <= 300 for url in urls)

    def test_raises_on_empty_file(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text("url,interval_seconds,regex_pattern\n")

        with pytest.raises(FileNotFoundError, match="No urls found"):
            load_urls(csv_file)

    def test_loads_from_environment_variable(
        self, tmp_path: Path, monkeypatch: pytest.MonkeyPatch
    ) -> None:
        csv_file = tmp_path / "custom_urls.csv"
        csv_file.write_text(
            "url,interval_seconds,regex_pattern\n"
            "https://custom.com,45,Custom\n"
        )
        monkeypatch.setenv("URLS_FILE", str(csv_file))

        urls = load_urls()

        assert len(urls) == 1
        assert urls[0].url == "https://custom.com"

    def test_handles_quoted_regex_with_comma(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            'url,interval_seconds,regex_pattern\n'
            'https://example.com,30,"pattern,with,commas"\n'
        )

        urls = load_urls(csv_file)

        assert urls[0].regex_pattern == "pattern,with,commas"

    def test_handles_regex_with_special_characters(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            'url,interval_seconds,regex_pattern\n'
            'https://example.com,30,"""userId"":\\s*\\d+"\n'
        )

        urls = load_urls(csv_file)

        assert urls[0].regex_pattern == '"userId":\\s*\\d+'

    def test_raises_on_invalid_interval(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,interval_seconds,regex_pattern\n"
            "https://example.com,3,pattern\n"  # 3 is below minimum of 5
        )

        with pytest.raises(ValueError, match="between 5 and 300"):
            load_urls(csv_file)

    def test_raises_on_missing_url_column(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "interval_seconds,regex_pattern\n"
            "30,pattern\n"
        )

        with pytest.raises(KeyError):
            load_urls(csv_file)

    def test_raises_on_missing_interval_column(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,regex_pattern\n"
            "https://example.com,pattern\n"
        )

        with pytest.raises(KeyError):
            load_urls(csv_file)

    def test_handles_missing_regex_column(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,interval_seconds\n"
            "https://example.com,30\n"
        )

        urls = load_urls(csv_file)

        assert urls[0].regex_pattern is None

    def test_loads_multiple_urls(self, tmp_path: Path) -> None:
        csv_file = tmp_path / "urls.csv"
        csv_file.write_text(
            "url,interval_seconds,regex_pattern\n"
            "https://one.com,10,\n"
            "https://two.com,20,pattern2\n"
            "https://three.com,30,pattern3\n"
        )

        urls = load_urls(csv_file)

        assert len(urls) == 3
        assert urls[0].url == "https://one.com"
        assert urls[1].url == "https://two.com"
        assert urls[2].url == "https://three.com"

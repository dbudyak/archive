# Website Monitor

Async service that monitors website availability and stores results in PostgreSQL.

## Features

- Per-URL configurable check intervals (5-300 seconds)
- Optional regex pattern matching on response body
- HTTP retries with configurable delay
- Database circuit breaker (stops retrying when DB is down)
- Connection pool with concurrency throttling
- Graceful shutdown (SIGTERM/SIGINT)
- Auto-running SQL migrations on startup

## Prerequisites

- **Python 3.12+**
- **Docker** (for local PostgreSQL option)
- **make**

Tested on macOS and Gentoo Linux. Not tested on Windows/WSL.

**macOS:**
```bash
brew install python@3.12 docker make
```

**Ubuntu/Debian:**
```bash
sudo apt update && sudo apt install python3.12 python3.12-venv docker.io make
```

## Running Locally

```bash
cp .env.example .env   # then edit with your credentials
```

**With Docker**
```bash
make start            # starts app + postgres

make logs             # tail logs
make monitor-db       # watch database in real-time
make stop             # cleanup
```

**With remote database**:
```bash
python -m venv venv && source venv/bin/activate
pip install -e ".[dev]"
make run
```

## Configuration

URLs are loaded from `config/urls.csv`:
```csv
url,interval_seconds,regex_pattern
https://example.com,30,Example Domain
https://api.github.com,60,"current_user_url"
https://httpbin.org/status/200,15,
```

Intervals must be 5-300 seconds. Regex is optional.

Database connection is configured via environment variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`). See `.env.example` for all options including pool sizes and timeouts.

## Project Layout

```
config/urls.csv          # monitored URLs
migrations/              # SQL migrations (auto-run on startup)
src/website_monitor/
  main.py                # entry point
  config/                # env loading, validation
  database/              # repository, migrations runner
  net/                   # http client, retry logic
  services/              # scheduler, url checker
tests/
  unit/                  # fast, no db needed
  integration/           # needs postgres
```

## Testing

```bash
make test              # unit tests
make integration-test  # spins up postgres, runs integration tests
make lint              # ruff + mypy
```

## Known Limitations

Smoke-tested positively against local and remote databases with 2000+ input unique urls

- Connection pool sized for managed PostgreSQL hobby tier. Adjust `DB_POOL_MAX_SIZE` for other setups.
- URL config changes need a restart (no hot reload).
- DB writes throttled to pool size (prevents pool exhaustion on startup burst with large number of URLs).

## What's Not Included

These would be nice for production but add complexity:

- Production-specific environment configurations and deployment files
- Health check endpoints
- Prometheus metrics, alerting integrations
- Rate limiting per domain
- Auto-blacklisting failing URLs
- Data retention / cleanup jobs
- Network outage detection (currently just retries and recovers)
- Pause all monitors when DB is down (circuit breaker skips saves, but checks continue)
- Handle error when input url configs are not present
- Handle error when migration files are not present
- AGENTS.md for optimal LLM synergies
- More refactorings :)

The circuit breaker for database failures is implemented though - if postgres goes down, the service won't spam reconnection attempts.

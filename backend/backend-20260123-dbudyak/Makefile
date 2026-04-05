.PHONY: help run start stop restart logs monitor-db lint test integration-test test-all clean

help:
	@echo "Local Development (no Docker, remote DB):"
	@echo "  make run          - Run app locally (requires .env with DB credentials)"
	@echo ""
	@echo "Docker Development (Docker + local PostgreSQL):"
	@echo "  make start    - Start app + PostgreSQL in Docker"
	@echo "  make stop     - Stop and remove containers"
	@echo "  make restart  - Rebuild and restart app only"
	@echo "  make logs     - Follow app logs"
	@echo "  make monitor-db       - Watch database writes"
	@echo ""
	@echo "Testing:"
	@echo "  make test              - Run unit tests only (fast, no DB needed)"
	@echo "  make integration-test  - Run integration tests (starts PostgreSQL)"
	@echo "  make test-all          - Run all tests (starts PostgreSQL)"
	@echo ""
	@echo "Quality:"
	@echo "  make lint              - Run ruff and mypy"
	@echo "  make clean             - Remove build artifacts"

run:
	@if [ ! -f .env ]; then echo "Error: .env file not found. Copy .env.example and configure."; exit 1; fi
	. venv/bin/activate && python -m website_monitor.main

start:
	docker compose -f docker-compose.dev.yml up --build -d
	@echo "Waiting for services to start..."
	@sleep 3
	docker compose -f docker-compose.dev.yml logs app --tail=10

stop:
	docker compose -f docker-compose.dev.yml down -v

restart:
	docker compose -f docker-compose.dev.yml up --build -d app
	@sleep 2
	docker compose -f docker-compose.dev.yml logs app --tail=20

logs:
	docker compose -f docker-compose.dev.yml logs -f app

monitor-db:
	@echo "Watching monitoring_events table (Ctrl+C to stop)..."
	@while true; do \
		clear; \
		echo "=== Monitoring Events (latest 10) ==="; \
		echo ""; \
		docker compose -f docker-compose.dev.yml exec -T postgres \
			psql -U postgres -d website_monitor -c \
			"SELECT id, url, status_code, response_time::int as ms, check_time::time as time, regex_match, error FROM monitoring_events ORDER BY id DESC LIMIT 10;"; \
		sleep 2; \
	done

test:
	@echo "Running unit tests..."
	. venv/bin/activate && pytest tests/unit/ --cov=website_monitor --cov-report=term-missing

integration-test:
	@echo "Starting PostgreSQL..."
	@docker compose -f docker-compose.dev.yml up -d postgres
	@echo "Waiting for PostgreSQL to be ready..."
	@until docker compose -f docker-compose.dev.yml exec -T postgres pg_isready -U postgres > /dev/null 2>&1; do sleep 1; done
	@echo "Creating test database..."
	@docker compose -f docker-compose.dev.yml exec -T postgres psql -U postgres -c "DROP DATABASE IF EXISTS website_monitor_test" > /dev/null 2>&1 || true
	@docker compose -f docker-compose.dev.yml exec -T postgres psql -U postgres -c "CREATE DATABASE website_monitor_test" > /dev/null 2>&1
	@echo "Running integration tests..."
	. venv/bin/activate && TEST_DB_HOST=localhost TEST_DB_PORT=5432 TEST_DB_NAME=website_monitor_test TEST_DB_USER=postgres TEST_DB_PASSWORD=postgres \
		pytest tests/integration/ -v

test-all:
	@echo "Starting PostgreSQL..."
	@docker compose -f docker-compose.dev.yml up -d postgres
	@echo "Waiting for PostgreSQL to be ready..."
	@until docker compose -f docker-compose.dev.yml exec -T postgres pg_isready -U postgres > /dev/null 2>&1; do sleep 1; done
	@echo "Creating test database..."
	@docker compose -f docker-compose.dev.yml exec -T postgres psql -U postgres -c "DROP DATABASE IF EXISTS website_monitor_test" > /dev/null 2>&1 || true
	@docker compose -f docker-compose.dev.yml exec -T postgres psql -U postgres -c "CREATE DATABASE website_monitor_test" > /dev/null 2>&1
	@echo "Running all tests..."
	. venv/bin/activate && TEST_DB_HOST=localhost TEST_DB_PORT=5432 TEST_DB_NAME=website_monitor_test TEST_DB_USER=postgres TEST_DB_PASSWORD=postgres \
		pytest tests/ --cov=website_monitor --cov-report=term-missing

lint:
	. venv/bin/activate && ruff check src tests --fix
	. venv/bin/activate && mypy src --fix

clean:
	rm -rf build/ dist/ *.egg-info .pytest_cache .mypy_cache .ruff_cache .coverage htmlcov/
	find . -type d -name __pycache__ -exec rm -rf {} + 2>/dev/null || true

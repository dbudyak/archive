FROM python:3.12-slim

WORKDIR /app

# Copy source and install
COPY pyproject.toml .
COPY src/ src/
COPY config/ config/
COPY migrations/ migrations/
RUN pip install --no-cache-dir .

# Create non-root user
RUN useradd --create-home appuser
USER appuser

CMD ["python", "-m", "website_monitor.main"]

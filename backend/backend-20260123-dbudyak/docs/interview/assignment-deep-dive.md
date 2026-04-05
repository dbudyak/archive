# Website Monitor Assignment - Deep Dive

## Assignment Requirements

**Core functionality:**
- Monitor website availability over the network
- Collect: request timestamp, response time, HTTP status code
- Optional: check page content against regex pattern
- Per-URL configurable interval (5-300 seconds)
- Log check failures to database

**Explicit constraints:**
- NO ORM libraries - use raw SQL only
- NO external scheduling libraries - implement concurrency with asyncio

**Evaluation priorities (from assignment):**
1. Simple and understandable code
2. Maintainability (main design goal)
3. Must work, be tested, handle errors
4. Should scale to thousands of sites
5. Code clarity over complexity

> "Anything unnecessarily complex, undocumented or untestable will be considered a minus."

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                          main.py                                │
│  - Load config from .env and urls.csv                          │
│  - Run migrations                                               │
│  - Create connection pool                                       │
│  - Wire dependencies                                            │
│  - Register signal handlers                                     │
│  - Start scheduler                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SchedulerService                           │
│  - Creates one task per URL                                     │
│  - Manages shutdown via asyncio.Event                          │
│  - Throttles DB writes via asyncio.Semaphore                   │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌──────────────────────────┐    ┌──────────────────────────────┐
│     UrlCheckService      │    │  MonitoringEventsRepository  │
│  - HTTP check with retry │    │  - Raw SQL INSERT            │
│  - Regex matching        │    │  - Circuit breaker           │
│  - Returns Result object │    │  - Timeout handling          │
└──────────────────────────┘    └──────────────────────────────┘
              │                               │
              ▼                               ▼
┌──────────────────────────┐    ┌──────────────────────────────┐
│       HttpClient         │    │    AsyncConnectionPool       │
│  - aiohttp wrapper       │    │  - psycopg_pool              │
│  - Body size limit       │    │  - min/max connections       │
└──────────────────────────┘    └──────────────────────────────┘
```

---

## Concurrency Model

### Why asyncio (not threading or multiprocessing)?

| Approach | Memory per task | Max concurrent | Best for |
|----------|-----------------|----------------|----------|
| Threading | ~1MB stack | Hundreds | CPU-bound with GIL release |
| Multiprocessing | Entire process | Tens | CPU-bound work |
| **asyncio** | **~1KB** | **Tens of thousands** | **I/O-bound** |

Website monitoring is **purely I/O-bound**:
- HTTP requests: waiting for network
- Database writes: waiting for PostgreSQL

With asyncio, monitoring 2000+ URLs requires only one thread, minimal memory.

### Event Loop Architecture

```python
# Single-threaded event loop
async def main():
    # All coroutines share one thread, cooperatively yielding at await points
    tasks = [asyncio.create_task(monitor_url(cfg)) for cfg in configs]
    await asyncio.gather(*tasks)
```

**Key insight:** While one coroutine awaits HTTP response, others can run. No OS context switches, no lock contention.

### Task-per-URL Pattern

```python
async def start(self, configs: list[UrlConfig]) -> None:
    tasks = [asyncio.create_task(self._monitor_url(config)) for config in configs]
    await asyncio.gather(*tasks, return_exceptions=True)
```

Each URL gets its own long-running coroutine that:
1. Performs HTTP check
2. Saves result to database
3. Sleeps for configured interval
4. Repeats until shutdown

**Why not a shared work queue?**
- Per-URL intervals (5-300s) are different
- No coordination needed between monitors
- Simpler mental model: one task = one URL

### Interruptible Sleep Pattern

**Problem:** `await asyncio.sleep(30)` blocks for full 30 seconds, even during shutdown.

**Solution:** Use `asyncio.Event` with timeout:

```python
self._shutdown_event = asyncio.Event()

# Instead of: await asyncio.sleep(interval)
with suppress(asyncio.TimeoutError):
    await asyncio.wait_for(
        self._shutdown_event.wait(),
        timeout=config.interval_seconds
    )
```

**How it works:**
- Normal operation: `wait_for` times out after interval → continue loop
- Shutdown: `shutdown_event.set()` wakes ALL waiters immediately → exit loop

This enables graceful shutdown in <1 second regardless of interval lengths.

---

## Connection Pool & Semaphore

### The Problem: Thundering Herd

With 500 URLs starting simultaneously:
1. All 500 make HTTP requests (fine - aiohttp handles this)
2. All 500 try to INSERT at once
3. Connection pool (15 connections) is exhausted
4. 485 coroutines wait for connections
5. Pool timeout → errors

### Solution: Semaphore Throttling

```python
def __init__(self, ..., max_concurrent_writes: int = 15):
    self._db_semaphore = asyncio.Semaphore(max_concurrent_writes)

async def _monitor_url(self, config: UrlConfig) -> None:
    result = await self._url_checker.check(config)
    async with self._db_semaphore:  # Only 15 concurrent DB operations
        await self._repository.save(result)
```

**Why semaphore value = pool size?**
- Each INSERT needs one connection
- Semaphore prevents more concurrent INSERTs than available connections
- No pool exhaustion, no timeout errors

**Why not just increase pool size?**
- Managed PostgreSQL hobby tier has connection limits
- More connections = more PostgreSQL memory
- 15 is practical default, configurable via `DB_MAX_CONCURRENT_WRITES`

### Pool Configuration

```python
pool = AsyncConnectionPool(
    settings.database_url,
    min_size=4,      # Pre-warmed connections
    max_size=15,     # Maximum concurrent
    timeout=120.0,   # Wait time for connection
)
```

**min_size:** Connections kept open even when idle. Avoids connection establishment latency.

**max_size:** Upper limit. Set to 15 for managed PostgreSQL hobby tier (leaves headroom for internal connections).

**timeout:** How long to wait if pool exhausted. With semaphore, should never be reached.

---

## Error Handling Strategy

### Two Error Patterns

| Error Type | Pattern | Rationale |
|------------|---------|-----------|
| HTTP failures | **Result pattern** | Expected operational data |
| Database failures | **Exception pattern** | Infrastructure errors |

### HTTP Errors → Result Object

```python
async def check(self, config: UrlConfig) -> UrlCheckResult:
    try:
        response = await with_retry(...)
        return UrlCheckResult(
            status_code=response.status_code,
            error=None,
        )
    except Exception as e:
        return UrlCheckResult(
            status_code=None,
            error=str(e),  # Error becomes data
        )
```

**Why Result pattern?**
- Monitoring failing URLs is the app's purpose
- HTTP failure is valuable data to record
- No exception propagation complexity

### Database Errors → Custom Exception

```python
class AppError(Exception):
    def __init__(self, message: str, *, retryable: bool = False):
        self.retryable = retryable

class DatabaseError(AppError):
    def __init__(self, message: str, original: Exception | None = None):
        super().__init__(message, retryable=True)
        self.original = original  # Preserve stack trace
```

**Why Exception pattern?**
- Database should be reliable infrastructure
- Errors need special handling (circuit breaker, logging)
- `retryable` flag enables smart retry logic

### Circuit Breaker

```python
@circuit(failure_threshold=5, recovery_timeout=30)
async def save(self, result: MonitoringEvent) -> None:
    ...
```

**Behavior:**
1. After 5 consecutive failures → circuit opens
2. All calls fail fast with `CircuitBreakerError` for 30s
3. After 30s → circuit half-open, allows one attempt
4. If success → circuit closes
5. If failure → circuit opens again

**Why needed?**
- Prevents hammering a down database
- Fails fast during outages (no connection timeouts)
- Auto-recovers when database returns

---

## HTTP Client Design

### Body Size Limit

```python
MAX_BODY_SIZE = 1024 * 1024  # 1MB

async def get(self, url: str, read_body: bool = False) -> HttpResponse:
    async with self._session.get(url, ...) as response:
        text = ""
        if read_body:
            raw = await response.content.read(MAX_BODY_SIZE)
            text = raw.decode("utf-8", errors="replace")
        return HttpResponse(status_code=response.status, text=text)
```

**Why limit body size?**
- Prevent memory exhaustion from huge responses
- 1MB is enough for regex matching on most pages
- `errors="replace"` handles invalid UTF-8 gracefully

### Conditional Body Reading

```python
needs_body = config.regex_pattern is not None
response = await self._client.get(config.url, read_body=needs_body)
```

**Optimization:** Only download body if regex pattern configured.

### Retry Logic

```python
async def with_retry(
    fn: Callable[[], Awaitable[T]],
    retry_count: int = 3,
    retry_delay: float = 1.0,
    on_retry: Callable[[int, Exception], Awaitable[None]] | None = None,
) -> T:
    for attempt in range(retry_count):
        try:
            return await fn()
        except Exception as e:
            if attempt < retry_count - 1:
                if on_retry:
                    await on_retry(attempt + 1, e)
                await asyncio.sleep(retry_delay)
    raise last_error
```

**Design decisions:**
- Generic function, not decorator (easier testing)
- Callback for logging without coupling
- Fixed delay (not exponential - keeps code simple)
- Retries all exceptions (network errors are transient)

---

## Graceful Shutdown

### Signal Handling

```python
for sig in (signal.SIGTERM, signal.SIGINT):
    asyncio.get_running_loop().add_signal_handler(sig, scheduler.shutdown)
```

**SIGTERM:** Sent by `docker stop`, Kubernetes, systemd

**SIGINT:** Sent by Ctrl+C

### Shutdown Flow

```
1. Signal received
2. scheduler.shutdown() called
3. _shutdown_event.set()
4. All monitor coroutines wake from wait_for()
5. Loop condition `while not self._shutdown_event.is_set()` fails
6. Coroutines exit normally
7. gather() completes
8. async context managers clean up (pool, session)
9. Process exits
```

**Key point:** No task cancellation needed. Coroutines exit naturally.

### Why Not Task Cancellation?

```python
# Alternative approach (not used):
for task in tasks:
    task.cancel()
await asyncio.gather(*tasks, return_exceptions=True)
```

**Problems with cancellation:**
- `CancelledError` propagates through call stack
- Need try/except in every function
- Resources might not clean up properly

**Current approach:**
- Event signals shutdown intent
- Coroutines check event and exit cleanly
- No exception handling complexity

---

## Database Design

### Schema

```sql
CREATE TABLE monitoring_events (
    id SERIAL PRIMARY KEY,
    url TEXT NOT NULL,
    check_time TIMESTAMPTZ NOT NULL,
    response_time REAL,
    status_code INTEGER,
    regex_match BOOLEAN,
    error TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_monitoring_events_url ON monitoring_events (url, check_time DESC);
```

**Design decisions:**

| Column | Type | Rationale |
|--------|------|-----------|
| id | SERIAL | Simple, sequential, good B-tree locality |
| url | TEXT | URLs can be long, TEXT has no length limit |
| check_time | TIMESTAMPTZ | When check was performed (with timezone) |
| response_time | REAL | Milliseconds, 4-byte float is sufficient |
| status_code | INTEGER | NULL when check fails (no response) |
| regex_match | BOOLEAN | NULL when no regex configured |
| error | TEXT | NULL on success, error message on failure |
| created_at | TIMESTAMPTZ | When row was inserted (audit trail) |

**Index:** Composite on (url, check_time DESC) for queries like "last N checks for URL X".

### Why Not UUID Primary Keys?

- Single database, no distributed ID generation needed
- SERIAL is 4 bytes vs UUID's 16 bytes
- Sequential insertion = better B-tree performance
- UUID would be needed for sharding/multi-master

### Why Not Separate Errors Table?

- Simpler queries (no JOINs for basic reporting)
- Error column is NULL for ~99% of rows (successful checks)
- Denormalization is acceptable at this scale

---

## Configuration Management

### Environment Variables

```python
# Required
DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD

# Optional with defaults
DB_POOL_MIN_SIZE=4
DB_POOL_MAX_SIZE=15
DB_POOL_TIMEOUT=120
DB_OPERATION_TIMEOUT=10.0
DB_MAX_CONCURRENT_WRITES=15
HTTP_RETRY_COUNT=3
HTTP_RETRY_DELAY=1.0
LOG_LEVEL=INFO
URLS_FILE=config/urls.csv
```

**Why environment variables?**
- 12-factor app compliance
- Easy container configuration
- Secrets don't end up in code/git

### URL Configuration (CSV)

```csv
url,interval_seconds,regex_pattern
https://example.com,30,Example Domain
https://api.github.com,60,"current_user_url"
https://httpbin.org/status/200,15,
```

**Validation:**
- URL must have http/https scheme
- Interval must be 5-300 seconds
- Regex is optional (empty = no check)

---

## Platform Limitations

### Python GIL

**What:** Global Interpreter Lock - only one thread executes Python bytecode at a time.

**Impact on this project:** None.

**Why?**
- asyncio is single-threaded by design
- GIL is released during I/O (network, disk)
- No CPU-bound work in this application

### asyncio Limitations

1. **Blocking calls block everything**
   ```python
   # WRONG: blocks entire event loop
   requests.get(url)  # sync library

   # CORRECT: yields control to event loop
   await session.get(url)  # async library
   ```

2. **Single-threaded = single core**
   - CPU-bound work would need `run_in_executor()` or multiprocessing
   - Not an issue here (no CPU-bound work)

3. **Error handling in tasks**
   - Exceptions in tasks don't propagate until awaited
   - `gather(return_exceptions=True)` prevents one failure from cancelling others

### aiohttp Limitations

1. **Session must be reused**
   - Creating session per request is expensive
   - Solution: Single session for entire application lifecycle

2. **Connection limits**
   - Default: 100 connections per host
   - May need tuning for high-volume single-host monitoring

### psycopg/PostgreSQL Limitations

1. **Connection limits**
   - PostgreSQL default: 100 connections
   - Managed PostgreSQL hobby tier: lower limits
   - Solution: Connection pool with appropriate max_size

2. **Prepared statements with pool**
   - psycopg handles this automatically
   - No manual statement management needed

---

## Scaling Considerations

### Current Capacity

Tested successfully with 2000+ URLs against local and remote databases.

### Bottlenecks

1. **Connection pool size**
   - 15 concurrent DB operations
   - Each INSERT takes ~1-5ms
   - Throughput: ~3000-15000 inserts/second theoretical

2. **Memory**
   - Each coroutine: ~1KB
   - 10,000 URLs: ~10MB for coroutines
   - Plus HTTP response buffers (limited to 1MB each)

3. **Network**
   - Single machine, single IP
   - May hit rate limits on target sites
   - Not an issue for monitoring diverse URLs

### Scaling Options (Not Implemented)

1. **Multiple instances**
   - Partition URLs across instances
   - Each instance monitors subset
   - Requires coordination (e.g., Redis, Consul)

2. **Batch inserts**
   - Collect results, INSERT multiple rows at once
   - Reduces round-trips but adds latency to individual records

3. **Write-ahead buffer**
   - Queue results locally if DB is slow
   - Drain queue when DB catches up

---

## Testing Strategy

### Unit Tests

```
tests/unit/
├── test_config.py           # Config loading, validation
├── test_http_client.py      # HTTP client edge cases
├── test_url_check_service.py # Check logic, regex matching
├── test_scheduler_service.py # Scheduler, semaphore, shutdown
└── test_repository.py       # SQL generation (mocked DB)
```

**No database required.** All external dependencies mocked.

### Integration Tests

```
tests/integration/
└── test_repository_integration.py  # Real PostgreSQL
```

**Requires PostgreSQL.** Tests actual SQL against real database.

### Test Patterns Used

1. **Async testing with pytest-asyncio**
   ```python
   @pytest.mark.asyncio
   async def test_check_success():
       ...
   ```

2. **Mocking aiohttp**
   ```python
   mock_response = Mock()
   mock_response.status = 200
   mock_session.get.return_value.__aenter__.return_value = mock_response
   ```

3. **Mocking asyncio primitives**
   ```python
   # Mock Event for shutdown testing
   mock_event = asyncio.Event()
   mock_event.set()  # Immediate shutdown
   ```

---

## Trade-offs Made

### Simplicity Over Features

| Feature | Status | Rationale |
|---------|--------|-----------|
| Hot reload config | Not implemented | Restart is acceptable for assignment |
| Health endpoint | Not implemented | Not required for core functionality |
| Prometheus metrics | Not implemented | Adds significant complexity |
| Rate limiting per domain | Not implemented | Parallel is simpler |
| URL blacklisting | Not implemented | Adds state management complexity |

### Single External Library for Circuit Breaker

**Why not implement ourselves?**
- `circuitbreaker` is well-tested, maintained
- Circuit breaker is not a scheduling library (allowed by constraints)
- Implementing correctly requires careful edge case handling

### Fixed Retry Delay (Not Exponential Backoff)

**Pros:**
- Simple to understand and debug
- Predictable behavior

**Cons:**
- Less polite to struggling servers
- Acceptable for assignment scope

### No Connection String URL Format

psycopg uses `key=value` format, yoyo-migrations needs `postgresql://` URL.

**Solution:** Conversion function in migrations.py

**Alternative:** Use SQLAlchemy-style URL everywhere (rejected - more dependencies)

---

## Potential Interview Questions

### Architecture

**Q: Why did you choose asyncio over threading?**
> Website monitoring is I/O-bound - waiting for HTTP responses and database writes. asyncio can handle thousands of concurrent connections with minimal memory (1KB per coroutine vs 1MB per thread). There's no CPU-bound work that would benefit from multi-threading.

**Q: How does the scheduler work?**
> One long-running coroutine per URL. Each loops: check → save → sleep → repeat. All coroutines run concurrently on single thread, yielding control at every `await`.

**Q: Why not use a work queue (producer/consumer pattern)?**
> Each URL has its own interval (5-300 seconds). A shared queue would need priority scheduling. Per-URL tasks are simpler - each manages its own timing independently.

### Concurrency

**Q: How do you handle graceful shutdown?**
> Signal handlers set an `asyncio.Event`. Each coroutine checks this event in its loop condition. The sleep between checks uses `wait_for(event.wait(), timeout=interval)` - this wakes immediately when the event is set, enabling <1 second shutdown.

**Q: What prevents database connection pool exhaustion?**
> A semaphore limits concurrent database writes to the pool size. Without it, 500 URLs finishing HTTP checks simultaneously would all compete for 15 connections, causing timeouts.

**Q: What's the difference between `asyncio.sleep()` and your interruptible sleep?**
> `asyncio.sleep(30)` blocks for the full 30 seconds even during shutdown. Using `wait_for(event.wait(), timeout=30)` allows immediate wakeup when the event is signaled, without waiting for the full interval.

### Error Handling

**Q: Why do HTTP errors become data but database errors are exceptions?**
> HTTP failures are expected operational data - monitoring failing URLs is the app's purpose. We record the failure in the database. Database failures are infrastructure errors - the database should be reliable. They need special handling: circuit breaker, logging, potential recovery.

**Q: How does the circuit breaker work?**
> After 5 consecutive database failures, the circuit opens. All subsequent calls fail fast without attempting connection for 30 seconds. Then it tries one request - if successful, circuit closes; if failed, it stays open another 30 seconds.

**Q: What happens during a network outage?**
> HTTP checks retry 3 times then record the error. Database writes fail, circuit breaker opens. Service continues running, making HTTP attempts (which fail) but skipping DB saves. When connectivity returns, circuit breaker closes and normal operation resumes automatically.

### Database

**Q: Why raw SQL instead of an ORM?**
> Assignment requirement explicitly prohibited ORMs. Also, for a single INSERT statement, an ORM adds complexity without benefit. Raw SQL is clearer and has no abstraction overhead.

**Q: Why SERIAL instead of UUID for primary key?**
> Single database, no need for distributed ID generation. SERIAL is 4 bytes vs UUID's 16 bytes. Sequential insertion gives better B-tree performance. UUID would be appropriate for sharding or multi-master replication.

**Q: Why a single table instead of normalizing errors?**
> Simpler queries - no JOINs needed for basic reporting. The error column is NULL for successful checks (~99% of records). At this scale, denormalization is acceptable. Separate table would be valuable if error analytics became a priority.

### Performance

**Q: How many URLs can this handle?**
> Tested with 2000+ URLs successfully. Theoretical limit depends on: connection pool size (throughput ~3000-15000 INSERTs/sec with 15 connections), memory (~1KB per coroutine), and network capacity.

**Q: What's the bottleneck?**
> Database write throughput. Each INSERT takes 1-5ms. With 15 concurrent connections and semaphore throttling, we can do roughly 3000-15000 writes per second. HTTP checks are parallelized without limit.

**Q: How would you scale beyond one machine?**
> Partition URLs across multiple instances. Each instance monitors a subset. Requires coordination mechanism (Redis, Consul, or database-based assignment). Could also batch INSERTs to reduce round-trips.

### Python Specific

**Q: Does the GIL affect this application?**
> No. The GIL is released during I/O operations. Since this application is purely I/O-bound (HTTP requests, database writes), all coroutines can make progress. The GIL would only matter for CPU-bound work.

**Q: Why Python 3.12+?**
> Modern type hints (`list[str]` instead of `List[str]`), better asyncio performance, `asyncio.TaskGroup` support. Also aligns with the "modern Python" requirement in the assignment.

### Testing

**Q: How do you test async code?**
> pytest-asyncio provides `@pytest.mark.asyncio` decorator. Mocking aiohttp responses requires context manager mocking (`__aenter__`). For integration tests, real PostgreSQL via Docker Compose.

**Q: How do you test the circuit breaker?**
> Mock the repository to raise `DatabaseError` multiple times, verify `CircuitBreakerError` is raised after threshold. Check recovery by succeeding after timeout.

### Operational

**Q: How would you deploy this in production?**
> Docker container with environment variables for configuration. Database on managed PostgreSQL (e.g. RDS). Orchestration via Kubernetes or ECS. Logs to stdout for aggregation. Would add health endpoint and Prometheus metrics.

**Q: What monitoring would you add?**
> Prometheus metrics: checks per second, error rates, response time histograms, circuit breaker state, pool usage. Structured JSON logging for log aggregation. Health endpoint for container orchestration.

**Q: How do you handle configuration changes?**
> Currently requires restart. For production, could add: file watcher for hot reload, API endpoint to trigger reload, or move configuration to database.

-- Create monitoring_events table
-- depends:

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

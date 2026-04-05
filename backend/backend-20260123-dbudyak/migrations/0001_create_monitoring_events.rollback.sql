-- Rollback: drop monitoring_events table

DROP INDEX IF EXISTS idx_monitoring_events_url;
DROP TABLE IF EXISTS monitoring_events;

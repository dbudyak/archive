-- Change url from TEXT to VARCHAR(2048)
-- depends: 0001_create_monitoring_events

ALTER TABLE monitoring_events ALTER COLUMN url TYPE VARCHAR(2048);

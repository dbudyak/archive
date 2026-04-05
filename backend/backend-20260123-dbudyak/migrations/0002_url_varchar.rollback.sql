-- Rollback: revert url to TEXT

ALTER TABLE monitoring_events ALTER COLUMN url TYPE TEXT;

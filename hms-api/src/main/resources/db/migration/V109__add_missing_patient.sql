ALTER TABLE patient_schema.patients

ADD COLUMN IF NOT EXISTS archive_reason VARCHAR(500);

ALTER TABLE patient_schema.patients

ADD COLUMN IF NOT EXISTS archived_at TIMESTAMP;

ALTER TABLE patient_schema.patients

ADD COLUMN IF NOT EXISTS archived_by VARCHAR(100);

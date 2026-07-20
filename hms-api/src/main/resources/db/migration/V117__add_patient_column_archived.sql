ALTER TABLE patient_schema.patients
ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE patient_schema.patients
SET archived=false
WHERE archived IS NULL;
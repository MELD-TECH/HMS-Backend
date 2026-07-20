ALTER TABLE patient_schema.patients
ADD COLUMN IF NOT EXISTS deceased_reversal_reason VARCHAR(500);

ALTER TABLE patient_schema.patients
ADD COLUMN IF NOT EXISTS deceased_reversed_at TIMESTAMP;

ALTER TABLE patient_schema.patients
ADD COLUMN IF NOT EXISTS deceased_reversed_by VARCHAR(100);
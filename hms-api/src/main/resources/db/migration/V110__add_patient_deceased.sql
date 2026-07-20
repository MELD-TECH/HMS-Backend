INSERT INTO identity_schema.permissions
(
    id,
    code,
    description,
    created_at,
    created_by,
    version
)
VALUES

(gen_random_uuid(),'PATIENT_DECEASED','Mark patient as deceased',CURRENT_TIMESTAMP,'SYSTEM',0);

ALTER TABLE patient_schema.patients

ADD COLUMN IF NOT EXISTS cause_of_death VARCHAR(200);

ALTER TABLE patient_schema.patients

ADD COLUMN IF NOT EXISTS deceased_notes VARCHAR(1000);
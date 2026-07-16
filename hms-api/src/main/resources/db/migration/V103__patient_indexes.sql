CREATE INDEX idx_patient_name
ON patient_schema.patients(last_name, first_name);

CREATE INDEX idx_patient_phone
ON patient_schema.patients(phone_number);

CREATE INDEX idx_patient_email
ON patient_schema.patients(email);
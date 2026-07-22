CREATE TABLE patient_schema.patient_contacts
(
    id UUID PRIMARY KEY,

    patient_id UUID NOT NULL,

    contact_type VARCHAR(30) NOT NULL,

    contact_value VARCHAR(150) NOT NULL,

    primary_contact BOOLEAN NOT NULL DEFAULT FALSE,

    verified BOOLEAN NOT NULL DEFAULT FALSE,

    verified_at TIMESTAMP,

    verified_by VARCHAR(100),

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_patient_contact_patient
        FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patients(id)
);

CREATE INDEX idx_patient_contact_patient
ON patient_schema.patient_contacts(patient_id);

CREATE INDEX idx_patient_contact_status
ON patient_schema.patient_contacts(status);

CREATE INDEX idx_patient_contact_type
ON patient_schema.patient_contacts(contact_type);

CREATE INDEX idx_patient_contact_primary
ON patient_schema.patient_contacts(primary_contact);

ALTER TABLE patient_schema.patient_contacts
ADD CONSTRAINT uk_patient_contact
UNIQUE
(
    patient_id,
    contact_type,
    contact_value
);

ALTER TABLE patient_schema.patient_contacts
ADD CONSTRAINT chk_patient_contact_status
CHECK
(
    status IN
    (
        'ACTIVE',
        'INACTIVE'
    )
);

ALTER TABLE patient_schema.patient_contacts
ADD CONSTRAINT chk_patient_contact_type
CHECK
(
    contact_type IN
    (
        'MOBILE',
        'HOME',
        'WORK',
        'EMAIL',
        'WHATSAPP',
        'EMERGENCY'
    )
);


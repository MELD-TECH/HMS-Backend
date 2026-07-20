CREATE TABLE patient_schema.patient_reverse_deceased_requests
(
    id UUID PRIMARY KEY,

    patient_id UUID NOT NULL,

    patient_number VARCHAR(30) NOT NULL,

    reason VARCHAR(500) NOT NULL,

    requested_by VARCHAR(100) NOT NULL,

    requested_at TIMESTAMP NOT NULL,

    status VARCHAR(30) NOT NULL,

    approved_by VARCHAR(100),

    approved_at TIMESTAMP,

    approval_comment VARCHAR(500),

    rejected_by VARCHAR(100),

    rejected_at TIMESTAMP,

    rejection_reason VARCHAR(500),

    created_at TIMESTAMP NOT NULL,

    created_by VARCHAR(100),

    updated_at TIMESTAMP,

    updated_by VARCHAR(100),

    version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE patient_schema.patient_reverse_deceased_requests
ADD CONSTRAINT fk_reverse_deceased_patient
FOREIGN KEY (patient_id)
REFERENCES patient_schema.patients(id);

CREATE UNIQUE INDEX uk_reverse_deceased_pending
ON patient_schema.patient_reverse_deceased_requests(patient_id)
WHERE status = 'PENDING';

CREATE INDEX idx_reverse_deceased_status
ON patient_schema.patient_reverse_deceased_requests(status);

CREATE INDEX idx_reverse_deceased_requested_by
ON patient_schema.patient_reverse_deceased_requests(requested_by);

CREATE INDEX idx_reverse_deceased_requested_at
ON patient_schema.patient_reverse_deceased_requests(requested_at);

CREATE INDEX idx_reverse_deceased_patient
ON patient_schema.patient_reverse_deceased_requests(patient_id);

ALTER TABLE patient_schema.patient_reverse_deceased_requests
ADD CONSTRAINT chk_reverse_deceased_status
CHECK (
    status IN
    (
        'PENDING',
        'APPROVED',
        'REJECTED',
        'CANCELLED'
    )
);


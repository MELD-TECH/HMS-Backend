--==========================================================
-- Link reverse deceased request to patient
--==========================================================

ALTER TABLE patient_schema.patient_reverse_deceased_requests

    ADD CONSTRAINT fk_reverse_request_patient

    FOREIGN KEY (patient_id)

    REFERENCES patient_schema.patients(id);

CREATE INDEX idx_reverse_request_patient

ON patient_schema.patient_reverse_deceased_requests(patient_id);
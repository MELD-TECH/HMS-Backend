package com.hms.common.exception;

import java.util.UUID;

public class PatientNotFoundException extends BusinessException {

    public PatientNotFoundException() {
        super("Patient record does not exist");
    }

    public PatientNotFoundException(UUID id) {
        super("Patient not found with id: " + id);
    }

    public PatientNotFoundException(String patientNumber) {
        super("Patient not found with patient number: " + patientNumber);
    }
}
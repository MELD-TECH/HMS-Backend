package com.hms.common.exception;

public class PatientAlreadyRestoredException 
extends BusinessException {

    public PatientAlreadyRestoredException(
            String patientNumber) {

        super("Restored patient '%s' already active."
                .formatted(patientNumber));
    }
}

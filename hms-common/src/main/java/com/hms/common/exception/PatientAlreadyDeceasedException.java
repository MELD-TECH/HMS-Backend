package com.hms.common.exception;

public class PatientAlreadyDeceasedException
extends BusinessException{

    public PatientAlreadyDeceasedException(
            String patientNumber) {

        super("Archived patient '%s' already deceased."
                .formatted(patientNumber));
    }
}

package com.hms.common.exception;

public class PatientArchivedException
        extends BusinessException {

    public PatientArchivedException(
            String patientNumber) {

        super("Archived patient '%s' cannot be updated."
                .formatted(patientNumber));
    }
}
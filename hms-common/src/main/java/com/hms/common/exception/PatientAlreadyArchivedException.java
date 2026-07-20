package com.hms.common.exception;

public class PatientAlreadyArchivedException
extends BusinessException{

    public PatientAlreadyArchivedException(
            String patientNumber) {

        super("Archived patient '%s' already exists."
                .formatted(patientNumber));
    }
}

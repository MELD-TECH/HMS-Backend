package com.hms.common.exception;

public class PatientAlreadyPendingRequestException extends BusinessException{

    public PatientAlreadyPendingRequestException(
            String patientNumber) {

        super("Patient '%s' already has pending request."
                .formatted(patientNumber));
    }
}

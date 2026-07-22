package com.hms.common.exception;

public class PatientContactAlreadyVerifiedException extends BusinessException {

    public PatientContactAlreadyVerifiedException(
            String patientNumber, String contact) {

        super("Patient '%s' contact already verified. ['%s']"
                .formatted(patientNumber, contact));
    }
}

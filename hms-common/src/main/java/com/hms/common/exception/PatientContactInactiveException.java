package com.hms.common.exception;

public class PatientContactInactiveException extends BusinessException {

    public PatientContactInactiveException(
            String contact) {

        super("Patient contact inactive. ['%s']"
                .formatted(contact));
    }
}

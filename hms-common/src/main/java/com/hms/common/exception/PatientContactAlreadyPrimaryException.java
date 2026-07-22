package com.hms.common.exception;

public class PatientContactAlreadyPrimaryException extends BusinessException {

    public PatientContactAlreadyPrimaryException(
            String contact) {

        super("Patient contact already primary. ['%s']"
                .formatted(contact));
    }
}

package com.hms.common.exception;

public class InvalidPatientContactException extends BusinessException {

    public InvalidPatientContactException(
            String contact) {

        super("Patient contact does not exist. ['%s']"
                .formatted(contact));
    }
}

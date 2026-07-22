package com.hms.common.exception;

public class DuplicatePatientContactException extends BusinessException {

    public DuplicatePatientContactException(
    		String type, String value) {

        super("Patient contact already exists for this type ['%s'] : - . '%s'"
                .formatted(type, value));
    }
}

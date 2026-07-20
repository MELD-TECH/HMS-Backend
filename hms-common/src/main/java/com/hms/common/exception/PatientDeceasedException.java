package com.hms.common.exception;

public class PatientDeceasedException
extends BusinessException {

public PatientDeceasedException(
    String patientNumber) {

super("Deceased patient '%s' cannot be updated."
        .formatted(patientNumber));
}
}
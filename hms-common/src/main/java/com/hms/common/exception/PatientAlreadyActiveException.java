package com.hms.common.exception;

public class PatientAlreadyActiveException
extends BusinessException{
	
    public PatientAlreadyActiveException(
            String patientNumber) {

        super("Patient '%s' already active."
                .formatted(patientNumber));
    }

}

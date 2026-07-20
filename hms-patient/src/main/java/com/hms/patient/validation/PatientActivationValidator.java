package com.hms.patient.validation;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.PatientAlreadyActiveException;
import com.hms.common.exception.PatientAlreadyDeceasedException;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class PatientActivationValidator {

    public void validate(Patient patient) {

        if (patient.getStatus() == PatientStatus.ACTIVE) {

            throw new PatientAlreadyActiveException(
                    "Patient is already active.");
        }

        if (patient.getStatus() == PatientStatus.DECEASED) {

            throw new PatientAlreadyDeceasedException(
                    "A deceased patient cannot be activated.");
        }
    }

}
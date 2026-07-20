package com.hms.patient.validation;

import org.springframework.stereotype.Component;

import com.hms.common.exception.PatientAlreadyArchivedException;
import com.hms.common.exception.PatientAlreadyDeceasedException;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class PatientArchiveValidator {

    public void validate(Patient patient) {

        if (patient.getStatus() == PatientStatus.ARCHIVED) {

            throw new PatientAlreadyArchivedException(
                    "Patient is already archived.");
        }

        if (patient.getStatus() == PatientStatus.DECEASED) {

            throw new PatientAlreadyDeceasedException(
                    "Deceased patients cannot be archived.");
        }
    }
}

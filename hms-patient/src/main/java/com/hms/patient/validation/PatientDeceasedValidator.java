package com.hms.patient.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.PatientAlreadyDeceasedException;
import com.hms.common.exception.PatientDeceasedException;
import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class PatientDeceasedValidator {

    public void validate(
            Patient patient,
            DeceasedPatientRequest request) {


        if (Boolean.TRUE.equals(patient.getDeceased())) {

            throw new PatientAlreadyDeceasedException(
                    "Patient has already been marked as deceased.");
        }

        if (patient.getStatus() != PatientStatus.ACTIVE) {

            throw new PatientDeceasedException(
                    "Only active patients can be marked as deceased.");
        }
        
        if (request.getDeceasedDate().isAfter(LocalDate.now())) {

            throw new BusinessException(
                    "Date of death cannot be in the future.");
        }

        if (request.getDeceasedDate()
                .isBefore(patient.getDateOfBirth())) {

            throw new BusinessException(
                    "Date of death cannot be before date of birth.");
        }
    }
}

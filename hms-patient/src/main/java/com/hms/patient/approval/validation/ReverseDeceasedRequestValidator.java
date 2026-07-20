package com.hms.patient.approval.validation;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class ReverseDeceasedRequestValidator {

    public void validate(Patient patient) {

        if (patient.getStatus() != PatientStatus.DECEASED) {
            throw new BusinessException(
                    "Patient is not marked as deceased.");
        }
    }
}
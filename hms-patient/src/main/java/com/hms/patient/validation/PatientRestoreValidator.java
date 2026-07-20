package com.hms.patient.validation;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.PatientNotFoundException;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class PatientRestoreValidator {

	public void validateRestore(
	        Patient patient) {

	    if (patient == null) {
	        throw new PatientNotFoundException(
	                "Patient not found.");
	    }

	    if (patient.getStatus()
	            == PatientStatus.DECEASED) {

	        throw new BusinessException(
	                "Deceased patients cannot be restored. Use Reverse Deceased workflow.");
	    }

	    if (patient.getStatus()
	            != PatientStatus.ARCHIVED) {

	        throw new BusinessException(
	                "Only archived patients can be restored.");
	    }
	}
}

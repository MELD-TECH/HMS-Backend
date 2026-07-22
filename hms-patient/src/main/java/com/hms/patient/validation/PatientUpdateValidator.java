package com.hms.patient.validation;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hms.common.exception.DuplicatePatientEmailException;
import com.hms.common.exception.DuplicatePatientPhoneException;
import com.hms.common.exception.InvalidPatientDateOfBirthException;
import com.hms.common.exception.OptimisticLockBusinessException;
import com.hms.common.exception.PatientArchivedException;
import com.hms.common.exception.PatientDeceasedException;
import com.hms.patient.dto.request.UpdatePatientRequest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientUpdateValidator {

    private final PatientRepository repository;

    public void validate(
            Patient patient,
            UpdatePatientRequest request) {

        
        if (!Objects.equals(request.getVersion(), patient.getVersion())) {
            throw new OptimisticLockBusinessException();
        }
        
    	validateStatus(patient);
    	
        validateDateOfBirth(request);

        validateEmail(patient.getId(), request);

        validatePhone(patient.getId(), request);
    }

    private void validateDateOfBirth(
            UpdatePatientRequest request) {

        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
        	throw new InvalidPatientDateOfBirthException(request.getDateOfBirth());
        }
    }

    private void validateEmail(
            UUID patientId,
            UpdatePatientRequest request) {

        if (!StringUtils.hasText(request.getEmail())) {
            return;
        }

        if (repository.existsByEmailIgnoreCaseAndIdNot(
                request.getEmail(),
                patientId)) {

            throw new DuplicatePatientEmailException(request.getEmail());
        }
    }

    private void validatePhone(
            UUID patientId,
            UpdatePatientRequest request) {

        if (!StringUtils.hasText(request.getPhoneNumber())) {
            return;
        }

        if (repository.existsByPhoneNumberAndIdNot(
                request.getPhoneNumber(),
                patientId)) {

            throw new DuplicatePatientPhoneException(request.getPhoneNumber());
        }
    }
    
    private void validateStatus(
            Patient patient) {

        if (patient.getStatus() == PatientStatus.ARCHIVED) {

            throw new PatientArchivedException(
                    patient.getPatientNumber());
        }

        if (patient.getStatus() == PatientStatus.DECEASED) {

            throw new PatientDeceasedException(
                    patient.getPatientNumber());
        }

    }
}

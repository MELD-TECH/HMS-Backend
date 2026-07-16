package com.hms.patient.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hms.common.exception.DuplicatePatientEmailException;
import com.hms.common.exception.DuplicatePatientPhoneException;
import com.hms.common.exception.InvalidPatientDateOfBirthException;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientRegistrationValidator {

    private final PatientRepository repository;

    public void validate(

            CreatePatientRequest request) {

        validateEmail(request);

        validatePhone(request);

        validateDateOfBirth(request);

    }
    
    private void validateEmail(

            CreatePatientRequest request) {

        if (request.getEmail() == null) {

            return;

        }

        if (repository.existsByEmailIgnoreCase(

                request.getEmail())) {

            throw new DuplicatePatientEmailException();

        }

    }
    
    private void validatePhone(

            CreatePatientRequest request) {

        if (request.getPhoneNumber() == null) {

            return;

        }

        if (repository.existsByPhoneNumber(

                request.getPhoneNumber())) {

            throw new DuplicatePatientPhoneException();

        }

    }
    
    private void validateDateOfBirth(

            CreatePatientRequest request) {

        if (request.getDateOfBirth()

                .isAfter(LocalDate.now())) {

            throw new InvalidPatientDateOfBirthException();

        }

    }
    
    
}

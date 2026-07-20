package com.hms.patient.validation;

import java.time.LocalDate;
import java.util.Objects;

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


    public void validate(CreatePatientRequest request) {

        Objects.requireNonNull(
                request,
                "Patient registration request must not be null.");

        validateDateOfBirth(request);

        validateEmail(request);

        validatePhone(request);
    }

    private void validateEmail(CreatePatientRequest request) {

        String email = request.getEmail();

        if (email == null || email.isBlank()) {
            return;
        }

        email = email.trim();

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicatePatientEmailException(request.getEmail());
        }
    }

    private void validatePhone(CreatePatientRequest request) {

        String phone = request.getPhoneNumber();

        if (phone == null || phone.isBlank()) {
            return;
        }

        phone = phone.trim();

        if (repository.existsByPhoneNumber(phone)) {
            throw new DuplicatePatientPhoneException(request.getPhoneNumber());
        }
    }

    private void validateDateOfBirth(CreatePatientRequest request) {

        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new InvalidPatientDateOfBirthException(request.getDateOfBirth());
        }
    }
    
}

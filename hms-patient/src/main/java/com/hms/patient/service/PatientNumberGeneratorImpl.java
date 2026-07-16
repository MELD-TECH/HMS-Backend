package com.hms.patient.service;

import java.time.Year;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.patient.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientNumberGeneratorImpl
        implements PatientNumberGenerator {

    private final PatientRepository repository;

    @Override
    @Transactional
    public String generate() {

        int year = Year.now().getValue();

        long sequence = validateSequence(
                repository.nextPatientSequence());

        return String.format(
                "HMS-%d-%06d",
                year,
                sequence);
    }
    
    private long validateSequence(long sequence) {

        if (sequence <= 0) {
            throw new IllegalStateException(
                    "Patient sequence must be greater than zero.");
        }

        return sequence;
    }

}

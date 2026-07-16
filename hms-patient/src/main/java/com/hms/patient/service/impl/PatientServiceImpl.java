package com.hms.patient.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.events.security.patient.PatientCreatedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.mapper.PatientMapper;
import com.hms.patient.repository.PatientRepository;
import com.hms.patient.service.PatientNumberGenerator;
import com.hms.patient.service.PatientService;
import com.hms.patient.validation.PatientRegistrationValidator;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl
        implements PatientService {

    private final PatientRepository repository;

    private final PatientMapper mapper;

    private final PatientNumberGenerator generator;

    private final PatientRegistrationValidator validator;

    private final SecurityEventPublisher publisher;
    
    @Override
    public PatientResponse register(

            CreatePatientRequest request) {

        validator.validate(request);

        Patient patient =

                mapper.toEntity(request);

        patient.setPatientNumber(

                generator.generate());

        patient.setStatus(

                PatientStatus.ACTIVE);

        patient.setDeceased(false);

        Patient saved =

                repository.save(patient);

        publishPatientCreated(saved);

        return mapper.toResponse(saved);

    }
    
    private void publishPatientCreated(

            Patient patient) {

        publisher.publish(

                new PatientCreatedEvent(

                        SecurityUtils.getCurrentUsername(),

                        patient.getPatientNumber()));

    }
}
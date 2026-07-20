package com.hms.patient.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.PatientNotFoundException;
import com.hms.events.security.patient.PatientActivatedEvent;
import com.hms.events.security.patient.PatientArchivedEvent;
import com.hms.events.security.patient.PatientCreatedEvent;
import com.hms.events.security.patient.PatientDeceasedEvent;

import com.hms.events.security.patient.PatientRestoredEvent;
import com.hms.events.security.patient.PatientUpdatedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.ActivatePatientRequest;
import com.hms.patient.dto.request.ArchivePatientRequest;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.dto.request.RestorePatientRequest;
import com.hms.patient.dto.request.UpdatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.lifecycle.PatientLifecycleManager;
import com.hms.patient.mapper.PatientMapper;
import com.hms.patient.repository.PatientRepository;
import com.hms.patient.service.PatientNumberGenerator;
import com.hms.patient.service.PatientService;
import com.hms.patient.validation.PatientActivationValidator;
import com.hms.patient.validation.PatientArchiveValidator;
import com.hms.patient.validation.PatientDeceasedValidator;
import com.hms.patient.validation.PatientRegistrationValidator;
import com.hms.patient.validation.PatientRestoreValidator;
import com.hms.patient.validation.PatientUpdateValidator;
import com.hms.security.util.SecurityUtils;

import jakarta.persistence.EntityManager;
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
    
    private final PatientUpdateValidator updateValidator;
    
    private final PatientArchiveValidator archiveValidator;
    
    private final PatientActivationValidator activationValidator;
    
    private final PatientLifecycleManager lifecycleManager;
    
    private final PatientDeceasedValidator deceasedValidator;
    
    private final PatientRestoreValidator restoreValidator;
    
    
    @Autowired
    private EntityManager entityManager;
    
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
    
    @Override
    @Transactional
    public PatientResponse update(
            UUID id,
            UpdatePatientRequest request) {

        Patient patient = repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));

        updateValidator.validate(patient, request);

        mapper.updateEntity(patient, request);

        Patient saved = repository.save(patient);
        
        repository.flush();

        entityManager.clear();

        publishUpdated(saved);

        return mapper.toResponse(saved);
    }
    
    
    @Override
    @Transactional
    public PatientResponse archive(
            UUID id,
            ArchivePatientRequest request) {

        Patient patient = repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));

        archiveValidator.validate(patient);

        lifecycleManager.archive(
                patient,
                request.getReason(),
                SecurityUtils.getCurrentUsername());

        Patient saved = repository.save(patient);
        
        repository.flush();

        entityManager.clear();

        publishArchived(saved, request);

        return mapper.toResponse(saved);
    }
    
    @Override
    @Transactional
    public PatientResponse activate(

            UUID id,

            ActivatePatientRequest request) {

        Patient patient = repository

                .findById(id)

                .orElseThrow(() ->

                        new PatientNotFoundException(id));

        activationValidator.validate(patient);

        lifecycleManager.activate(patient);

        Patient saved = repository.save(patient);
        
        
        repository.flush();

        entityManager.clear();

        publishActivated(saved, request);

        return mapper.toResponse(saved);
    }
    
    @Override
    @Transactional
    public PatientResponse markDeceased(
            UUID id,
            DeceasedPatientRequest request) {

        Patient patient = repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));
       
        deceasedValidator.validate(patient, request);

        lifecycleManager.markDeceased(
                patient,
                request.getDeceasedDate(),
                request.getCauseOfDeath(),
                request.getDeceasedNotes());

        Patient saved = repository.save(patient);           
        
        repository.flush();

        entityManager.clear();
        
        publishDeceased(saved, request);

        return mapper.toResponse(saved);
    }
    
    @Override
    public PatientResponse restore(
            UUID patientId,
            RestorePatientRequest request) {

        Patient patient =
                repository.findById(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient not found"));

        restoreValidator.validateRestore(patient);

        lifecycleManager.restore(patient);

        Patient saved =
                repository.save(patient);

        repository.flush();

        entityManager.clear();
        
        publishRestore(saved, request);

        return mapper.toResponse(saved);
    }
        
    private void publishPatientCreated(

            Patient patient) {

        if (patient.getId() == null) {
            throw new IllegalStateException(
                    "Patient ID must not be null before publishing creation event.");
        }
        
        publisher.publish(

                new PatientCreatedEvent(

                        SecurityUtils.getCurrentUsername(),
                        
                        patient.getId().toString(),

                        patient.getPatientNumber()));

    }
    
    
    private void publishUpdated(
            Patient patient) {

        if (patient.getId() == null) {
            throw new IllegalStateException(
                    "Patient ID must not be null before publishing creation event.");
        }
        
        publisher.publish(
                new PatientUpdatedEvent(
                        SecurityUtils.getCurrentUsername(),
                        patient.getId().toString(),
                        patient.getPatientNumber()));
    }
    

    private void publishArchived(
            Patient patient,
            ArchivePatientRequest request) {

        if (patient.getId() == null) {
            throw new IllegalStateException(
                    "Patient ID must not be null before publishing creation event.");
        }
        
        publisher.publish(

                new PatientArchivedEvent(

                        SecurityUtils.getCurrentUsername(),
                        
                        patient.getId().toString(),

                        patient.getPatientNumber(),

                        request.getReason()));
    }
    
    private void publishActivated(

            Patient patient,

            ActivatePatientRequest request) {

        publisher.publish(

                new PatientActivatedEvent(

                        SecurityUtils.getCurrentUsername(),

                        patient.getId().toString(),

                        patient.getPatientNumber(),

                        request.getReason()));
    }
    
    private void publishDeceased(
            Patient patient,
            DeceasedPatientRequest request) {

        publisher.publish(

                new PatientDeceasedEvent(

                        SecurityUtils.getCurrentUsername(),

                        patient.getId().toString(),

                        patient.getPatientNumber(),

                        request.getCauseOfDeath()));
    }
    
    private void publishRestore(

            Patient patient,

            RestorePatientRequest request) {

        publisher.publish(

                new PatientRestoredEvent(

                        SecurityUtils.getCurrentUsername(),

                        patient.getId().toString(),

                        patient.getPatientNumber(),

                        request.getReason()));
    }
    
}
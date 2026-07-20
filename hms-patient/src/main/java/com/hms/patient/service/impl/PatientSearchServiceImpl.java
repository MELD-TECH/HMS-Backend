package com.hms.patient.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.PatientNotFoundException;
import com.hms.events.security.patient.PatientViewedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.SearchPatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.dto.response.PatientSummaryResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.mapper.PatientMapper;
import com.hms.patient.repository.PatientRepository;
import com.hms.patient.service.PatientSearchService;
import com.hms.patient.specification.PatientSpecification;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientSearchServiceImpl
        implements PatientSearchService {

    private final PatientRepository repository;

    private final PatientMapper mapper;

    private final SecurityEventPublisher publisher;

    @Override
    public Page<PatientSummaryResponse> search(

            SearchPatientRequest request,

            Pageable pageable) {

        return repository

                .findAll(

                        PatientSpecification.search(request),

                        pageable)

                .map(mapper::toSummary);
    }

    @Override
    public PatientResponse findById(

            UUID id) {

        Patient patient = getPatientById(id);;

        publishViewed(patient);

        return mapper.toResponse(patient);
    }

    @Override
    public PatientResponse findByPatientNumber(

            String patientNumber) {

        Patient patient = getPatientByNumber(patientNumber);

        publishViewed(patient);

        return mapper.toResponse(patient);
    }

    private void publishViewed(
            Patient patient) {

        publisher.publish(

                new PatientViewedEvent(

                        SecurityUtils.getCurrentUsername(),
                        
                        patient.getId().toString(),

                        patient.getPatientNumber()));
    }

    private Patient getPatientById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new PatientNotFoundException(id));
    }

    private Patient getPatientByNumber(String patientNumber) {

        return repository.findByPatientNumber(patientNumber)
                .orElseThrow(() ->
                        new PatientNotFoundException(patientNumber));
    }
}

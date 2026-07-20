package com.hms.patient.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hms.patient.dto.request.SearchPatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.dto.response.PatientSummaryResponse;

public interface PatientSearchService {

    Page<PatientSummaryResponse> search(
            SearchPatientRequest request,
            Pageable pageable);

    PatientResponse findById(UUID id);

    PatientResponse findByPatientNumber(String patientNumber);
}
package com.hms.patient.controller;

import com.hms.patient.approval.dto.request.RequestReverseDeceasedRequest;
import com.hms.patient.approval.dto.response.ReverseDeceasedRequestResponse;
import com.hms.patient.approval.service.PatientApprovalService;
import com.hms.patient.dto.request.ActivatePatientRequest;
import com.hms.patient.dto.request.ArchivePatientRequest;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.dto.request.RestorePatientRequest;
import com.hms.patient.dto.request.SearchPatientRequest;
import com.hms.patient.dto.request.UpdatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.dto.response.PatientSummaryResponse;
import com.hms.patient.service.PatientSearchService;
import com.hms.patient.service.PatientService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    private final PatientSearchService patientSearchService;
    
    private final PatientApprovalService patientApprovalService;
    
    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT_CREATE')")
    public ResponseEntity<PatientResponse> register(
            @Valid
            @RequestBody CreatePatientRequest request) {

        PatientResponse response =
                patientService.register(request);

        URI location = URI.create(
                "/api/v1/patients/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    
    @GetMapping
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public Page<PatientSummaryResponse> search(
            SearchPatientRequest request,
            Pageable pageable) {

        return patientSearchService.search(
                request,
                pageable);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public PatientResponse findById(
            @PathVariable UUID id) {

        return patientSearchService.findById(id);
    }
    
    @GetMapping("/number/{patientNumber}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public PatientResponse findByPatientNumber(
            @PathVariable String patientNumber) {

        return patientSearchService.findByPatientNumber(
                patientNumber);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<PatientResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request) {

        PatientResponse response =
                patientService.update(id, request);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('PATIENT_ARCHIVE')")
    public ResponseEntity<PatientResponse> archive(

            @PathVariable UUID id,

            @Valid
            @RequestBody ArchivePatientRequest request) {

        PatientResponse response =
                patientService.archive(id, request);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('PATIENT_RESTORE')")
    public ResponseEntity<PatientResponse> restore(

            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            RestorePatientRequest request) {

        PatientResponse response =
                patientService.restore(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('PATIENT_ACTIVATE')")
    public ResponseEntity<PatientResponse> activate(

            @PathVariable UUID id,

            @Valid

            @RequestBody ActivatePatientRequest request) {

        return ResponseEntity.ok(

                patientService.activate(id, request));
    }
    
    @PatchMapping("/{id}/deceased")
    @PreAuthorize("hasAuthority('PATIENT_DECEASED')")
    public ResponseEntity<PatientResponse> markDeceased(

            @PathVariable UUID id,

            @Valid
            @RequestBody DeceasedPatientRequest request) {

        return ResponseEntity.ok(

                patientService.markDeceased(
                        id,
                        request));
    }
   
    @PostMapping("/{id}/reverse-deceased/request")
    @PreAuthorize("hasAuthority('PATIENT_REVERSE_DECEASED_REQUEST')")
    public ResponseEntity<ReverseDeceasedRequestResponse>
    requestReverseDeceased(
            @PathVariable UUID id,
            @Valid @RequestBody RequestReverseDeceasedRequest request) {

        return ResponseEntity.ok(
                patientApprovalService
                        .requestReverseDeceased(id, request));
    }

}
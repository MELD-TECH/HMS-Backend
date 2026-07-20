package com.hms.patient.approval.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.patient.approval.dto.request.ApproveReverseDeceasedRequest;
import com.hms.patient.approval.dto.request.RejectReverseDeceasedRequest;
import com.hms.patient.approval.service.PatientApprovalService;
import com.hms.patient.dto.response.PatientResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/patient-approval")
@RequiredArgsConstructor
public class PatientApprovalController {

    private final PatientApprovalService patientApprovalService;

    @PostMapping("/reverse-deceased/{requestId}/approve")
    @PreAuthorize("hasAuthority('PATIENT_REVERSE_DECEASED_APPROVE')")
    public ResponseEntity<PatientResponse> approve(
            @PathVariable UUID requestId,
            @Valid @RequestBody ApproveReverseDeceasedRequest request) {

        return ResponseEntity.ok(
                patientApprovalService
                        .approveReverseDeceased(requestId, request));
    }

    @PostMapping("/reverse-deceased/{requestId}/reject")
    @PreAuthorize("hasAuthority('PATIENT_REVERSE_DECEASED_REJECT')")
    public ResponseEntity<Void> reject(
            @PathVariable UUID requestId,
            @Valid @RequestBody RejectReverseDeceasedRequest request) {

        patientApprovalService
                .rejectReverseDeceased(requestId, request);

        return ResponseEntity.noContent().build();
    }
}
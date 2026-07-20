package com.hms.patient.approval.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.patient.approval.enums.ApprovalStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReverseDeceasedRequestResponse {

    private UUID id;

    private UUID patientId;

    private String patientNumber;

    private String reason;

    private ApprovalStatus status;

    private String requestedBy;

    private LocalDateTime requestedAt;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private String rejectedBy;

    private LocalDateTime rejectedAt;

    private String rejectionReason;
    
    private boolean pending;

    private boolean completed;
}
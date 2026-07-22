package com.hms.patient.approval.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.common.BaseEntity;
import com.hms.patient.approval.enums.ApprovalStatus;
import com.hms.patient.entity.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    schema = "patient_schema",
    name = "patient_reverse_deceased_requests"
)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientReverseDeceasedRequest
        extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String patientNumber;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false, length = 100)
    private String requestedBy;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private String rejectedBy;

    private LocalDateTime rejectedAt;

    @Column(length = 500)
    private String rejectionReason;
   
    @Column(length = 500)
    private String approvalComment;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reverse_request_patient"))
    private Patient patient;
    
    public void approve(
            String approver,
            String comment) {

        if (status != ApprovalStatus.PENDING) {

            throw new IllegalStateException(
                    "Request has already been processed.");
        }

        status = ApprovalStatus.APPROVED;

        approvedBy = approver;

        approvedAt = LocalDateTime.now();

        rejectionReason = null;
        
        approvalComment = comment;
    }
    
    public void reject(
            String approver,
            String reason) {

        if (status != ApprovalStatus.PENDING) {

            throw new IllegalStateException(
                    "Request has already been processed.");
        }

        status = ApprovalStatus.REJECTED;

        rejectedBy = approver;

        rejectedAt = LocalDateTime.now();

        rejectionReason = reason;
    }
    
}

package com.hms.events.security.patient;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientDeceasedReversalRejectedEvent
        extends SecurityEvent {

    private final String patientNumber;

    private final String requestedBy;

    private final String rejectionReason;

    public PatientDeceasedReversalRejectedEvent(

            String username,

            String entityId,

            String patientNumber,

            String requestedBy,

            String rejectionReason) {

        super(

                username,

                "PATIENT",

                entityId);

        this.patientNumber = patientNumber;

        this.requestedBy = requestedBy;

        this.rejectionReason = rejectionReason;
    }

    @Override
    public String action() {

        return AuditAction
                .PATIENT_DECEASED_REVERSAL_REJECTED
                .name();
    }

    @Override
    public String details() {

        return String.format(

                "Reverse deceased request rejected for patient [%s]. Requested by [%s]. Reason: %s",

                patientNumber,

                requestedBy,

                rejectionReason);
    }
}
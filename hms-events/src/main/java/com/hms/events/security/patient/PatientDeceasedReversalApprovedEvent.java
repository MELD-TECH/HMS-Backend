package com.hms.events.security.patient;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientDeceasedReversalApprovedEvent
        extends SecurityEvent {

    private final String patientNumber;

    private final String requestedBy;

    public PatientDeceasedReversalApprovedEvent(

            String username,

            String entityId,

            String patientNumber,

            String requestedBy) {

        super(

                username,

                "PATIENT",

                entityId);

        this.patientNumber = patientNumber;

        this.requestedBy = requestedBy;
    }

    @Override
    public String action() {

        return AuditAction
                .PATIENT_DECEASED_REVERSAL_APPROVED
                .name();
    }

    @Override
    public String details() {

        return String.format(

                "Reverse deceased request approved for patient [%s]. Requested by [%s].",

                patientNumber,

                requestedBy);
    }
}
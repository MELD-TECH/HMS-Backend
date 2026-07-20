package com.hms.events.security.patient;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientActivatedEvent extends SecurityEvent {

    private final String patientNumber;

    private final String reason;

    public PatientActivatedEvent(
            String username,
            String entityId,
            String patientNumber,
            String reason) {

        super(
                username,
                "PATIENT",
                entityId);

        this.patientNumber = patientNumber;
        this.reason = reason;
    }

    @Override
    public String action() {

        return AuditAction.PATIENT_ACTIVATED.name();
    }

    @Override
    public String details() {

        return String.format(
                "Patient [%s] activated. Reason: %s",
                patientNumber,
                reason);
    }
}
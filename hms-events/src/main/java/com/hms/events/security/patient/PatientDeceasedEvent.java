package com.hms.events.security.patient;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientDeceasedEvent
        extends SecurityEvent {

    private final String patientNumber;

    private final String causeOfDeath;

    public PatientDeceasedEvent(
            String username,
            String entityId,
            String patientNumber,
            String causeOfDeath) {

        super(
                username,
                "PATIENT",
                entityId);

        this.patientNumber = patientNumber;
        this.causeOfDeath = causeOfDeath;
    }

    @Override
    public String action() {

        return AuditAction.PATIENT_DECEASED.name();
    }

    @Override
    public String details() {

        return String.format(
                "Patient [%s] marked as deceased. Cause: %s",
                patientNumber,
                causeOfDeath);
    }
}

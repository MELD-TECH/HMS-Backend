package com.hms.events.security.patient.contact;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientContactUpdatedEvent extends SecurityEvent {

    private final String patientNumber;

    private final String contactType;

    public PatientContactUpdatedEvent(
            String username,
            String entityId,
            String patientNumber,
            String contactType) {

        super(username, "PATIENT_CONTACT", entityId);

        this.patientNumber = patientNumber;
        this.contactType = contactType;
    }

    @Override
    public String action() {

        return AuditAction.PATIENT_CONTACT_UPDATED.name();

    }

    @Override
    public String details() {

        return String.format(
                "Patient contact updated. Patient=[%s], Type=[%s]",
                patientNumber,
                contactType);

    }

}
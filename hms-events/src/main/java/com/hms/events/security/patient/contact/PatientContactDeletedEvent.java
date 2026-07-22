package com.hms.events.security.patient.contact;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientContactDeletedEvent extends SecurityEvent {

    private final String patientNumber;

    private final String contactType;

    public PatientContactDeletedEvent(
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

        return AuditAction.PATIENT_CONTACT_DELETED.name();

    }

    @Override
    public String details() {

        return String.format(
                "Patient contact deactivated. Patient=[%s], Type=[%s]",
                patientNumber,
                contactType);

    }

}
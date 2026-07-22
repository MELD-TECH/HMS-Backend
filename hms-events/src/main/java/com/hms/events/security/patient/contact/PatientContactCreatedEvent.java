package com.hms.events.security.patient.contact;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientContactCreatedEvent extends SecurityEvent {

    private final String patientNumber;

    private final String contactType;

    private final String contactValue;

    public PatientContactCreatedEvent(
            String username,
            String entityId,
            String patientNumber,
            String contactType,
            String contactValue) {

        super(username, "PATIENT_CONTACT", entityId);

        this.patientNumber = patientNumber;
        this.contactType = contactType;
        this.contactValue = contactValue;
    }

    @Override
    public String action() {

        return AuditAction.PATIENT_CONTACT_CREATED.name();

    }

    @Override
    public String details() {

        return String.format(
                "Patient contact created. Patient=[%s], Type=[%s], Value=[%s]",
                patientNumber,
                contactType,
                contactValue);

    }

}
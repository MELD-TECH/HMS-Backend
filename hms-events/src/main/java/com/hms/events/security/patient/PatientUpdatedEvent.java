package com.hms.events.security.patient;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

import lombok.Getter;

@Getter
public class PatientUpdatedEvent
        extends SecurityEvent {

	private final String patientNumber;
	
    public PatientUpdatedEvent(
            String username,
            String entityId,
            String patientNumber) {

        super(
                username,
                "PATIENT",
                entityId);
        
        this.patientNumber = patientNumber;

    }
    
        @Override
        public String action() {

            return AuditAction.PATIENT_UPDATED.name();

        }

        @Override
        public String details() {

            return String.format(
                    "Patient record updated: [%s]",
                    patientNumber
                    );
        } 
}

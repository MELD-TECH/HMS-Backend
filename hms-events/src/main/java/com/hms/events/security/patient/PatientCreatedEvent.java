package com.hms.events.security.patient;

import com.hms.audit.security.enums.AuditAction;
import com.hms.events.security.SecurityEvent;

public class PatientCreatedEvent
extends SecurityEvent {

    private final String patientNumber;
    
	public PatientCreatedEvent(
		    String username,
		    String entityId,
		    String patientNumber) {

		super(username,"PATIENT",entityId);
		
		this.patientNumber = patientNumber;
		}

		@Override
		public String action() {

		return AuditAction.PATIENT_CREATED.name();
		}

		@Override
		public String details() {

		    return String.format(
		            "Patient [%s] registered.",
		            patientNumber
		            );
		}
}

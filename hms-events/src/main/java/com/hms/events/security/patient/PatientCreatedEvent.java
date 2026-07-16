package com.hms.events.security.patient;

import com.hms.events.security.SecurityEvent;

public class PatientCreatedEvent
extends SecurityEvent {

	public PatientCreatedEvent(
		    String username,
		    String entityId) {

		super(username,"USER",entityId);
		}

		@Override
		public String action() {

		return "PATIENT_CREATED";
		}

		@Override
		public String details() {

		return "patient created";
		}
}

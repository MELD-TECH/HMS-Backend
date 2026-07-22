package com.hms.patient.contact.lifecycle;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.hms.patient.contact.dto.request.UpdatePatientContactRequest;
import com.hms.patient.contact.entity.PatientContact;
import com.hms.patient.contact.enums.ContactStatus;

@Component
public class PatientContactLifecycleManager {

	public void initialize(
	        PatientContact contact) {
		
		contact.setStatus(ContactStatus.ACTIVE);

		contact.setVerified(false);

		contact.setVerifiedAt(null);

		contact.setVerifiedBy(null);

		if (contact.getPrimaryContact() == null) {

		    contact.setPrimaryContact(false);
		}
	}
	
	public void update(
	        PatientContact contact,
	        UpdatePatientContactRequest request) {
		
		contact.setContactType(
		        request.getContactType());

		contact.setContactValue(
		        request.getContactValue());

		contact.setPrimaryContact(
		        request.getPrimaryContact());
	}
	
	public void deactivate(
	        PatientContact contact) {
		
		contact.setStatus(ContactStatus.INACTIVE);
	}
	
	public void activate(
	        PatientContact contact) {
		
		contact.setStatus(ContactStatus.ACTIVE);
	}
	
	public void verify(
	        PatientContact contact,
	        String username) {
		
		contact.setVerified(true);

		contact.setVerifiedAt(LocalDateTime.now());

		contact.setVerifiedBy(username);
	}
	
	public void makePrimary(
	        PatientContact contact) {
		
		contact.setPrimaryContact(true);
	}
	
	public void removePrimary(
	        PatientContact contact) {
		
		contact.setPrimaryContact(false);
	}
}

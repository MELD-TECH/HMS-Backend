package com.hms.patient.contact.mapper;

import org.springframework.stereotype.Component;

import com.hms.patient.contact.dto.request.CreatePatientContactRequest;
import com.hms.patient.contact.dto.request.UpdatePatientContactRequest;
import com.hms.patient.contact.dto.response.PatientContactResponse;
import com.hms.patient.contact.entity.PatientContact;
import com.hms.patient.contact.enums.ContactStatus;
import com.hms.patient.entity.Patient;

@Component
public class PatientContactMapper {

	public PatientContactResponse toResponse(
	        PatientContact contact) {

	    if (contact == null) {
	        return null;
	    }

	    return PatientContactResponse.builder()

	            .id(contact.getId())

	            .patientId(contact.getPatient().getId())

	            .contactType(contact.getContactType())

	            .contactValue(contact.getContactValue())

	            .primaryContact(contact.getPrimaryContact())

	            .verified(contact.getVerified())

	            .verifiedAt(contact.getVerifiedAt())

	            .verifiedBy(contact.getVerifiedBy())

	            .status(contact.getStatus())

	            .createdAt(contact.getCreatedAt())

	            .updatedAt(contact.getUpdatedAt())

	            .createdBy(contact.getCreatedBy())

	            .updatedBy(contact.getUpdatedBy())

	            .version(contact.getVersion())

	            .build();
	}
	
	public PatientContact toEntity(
	        Patient patient,
	        CreatePatientContactRequest request) {

	    return PatientContact.builder()

	            .patient(patient)

	            .contactType(request.getContactType())

	            .contactValue(request.getContactValue())

	            .primaryContact(request.getPrimaryContact())

	            .verified(false)

	            .status(ContactStatus.ACTIVE)

	            .build();
	}
	
	public void updateEntity(
	        PatientContact entity,
	        UpdatePatientContactRequest request) {

	    entity.setContactType(request.getContactType());

	    entity.setContactValue(request.getContactValue());

	    entity.setPrimaryContact(request.getPrimaryContact());
	}
	
	
}

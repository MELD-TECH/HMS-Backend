package com.hms.patient.contact.validation;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.hms.common.exception.DuplicatePatientContactException;
import com.hms.common.exception.InvalidPatientContactException;
import com.hms.common.exception.OptimisticLockBusinessException;
import com.hms.common.exception.PatientArchivedException;
import com.hms.common.exception.PatientContactAlreadyPrimaryException;
import com.hms.common.exception.PatientContactAlreadyVerifiedException;
import com.hms.common.exception.PatientContactInactiveException;
import com.hms.common.exception.PatientDeceasedException;
import com.hms.patient.contact.dto.request.CreatePatientContactRequest;
import com.hms.patient.contact.dto.request.UpdatePatientContactRequest;
import com.hms.patient.contact.entity.PatientContact;
import com.hms.patient.contact.enums.ContactStatus;
import com.hms.patient.contact.enums.ContactType;
import com.hms.patient.contact.repository.PatientContactRepository;
import com.hms.patient.entity.Patient;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientContactValidator {

	private final PatientContactRepository repository;
	
	private static final Pattern PHONE_PATTERN =
	        Pattern.compile("^(\\+234|0)[789][01][0-9]{8}$");
	
	public void validateCreate(
	        Patient patient,
	        CreatePatientContactRequest request) {

	    validatePatient(patient);

	    validateContactValue(
	            request.getContactType(),
	            request.getContactValue());

	    validateDuplicate(
	            patient.getId(),
	            request.getContactType(),
	            request.getContactValue());
	}
	
	public void validateUpdate(
	        PatientContact contact,
	        UpdatePatientContactRequest request) {

	    validatePatient(contact.getPatient());

	    validateVersion(contact, request);

	    validateActive(contact);

	    validateContactValue(
	            request.getContactType(),
	            request.getContactValue());

	    validateDuplicate(
	            contact,
	            request);
	}
	
	public void validateDelete(
	        PatientContact contact) {

	    validatePatient(contact.getPatient());

	    validateActive(contact);
	}
	
	public void validateVerify(
	        PatientContact contact) {

	    validatePatient(contact.getPatient());

	    validateActive(contact);

	    if (Boolean.TRUE.equals(contact.getVerified())) {

	        throw new PatientContactAlreadyVerifiedException(
	                contact.getPatient().getPatientNumber(), contact.getId().toString());
	    }
	}
	
	public void validateSetPrimary(
	        PatientContact contact) {

	    validatePatient(contact.getPatient());

	    validateActive(contact);

	    if (Boolean.TRUE.equals(contact.getPrimaryContact())) {

	        throw new PatientContactAlreadyPrimaryException(
	                contact.getId().toString());
	    }
	}
	
	private void validatePatient(
	        Patient patient) {

	    if (patient.getArchived()) {

	        throw new PatientArchivedException(
	                patient.getPatientNumber());
	    }

	    if (patient.getDeceased()) {

	        throw new PatientDeceasedException(
	                patient.getPatientNumber());
	    }
	}
	
	private void validateVersion(
	        PatientContact contact,
	        UpdatePatientContactRequest request) {

	    if (!Objects.equals(
	            contact.getVersion(),
	            request.getVersion())) {

	        throw new OptimisticLockBusinessException();
	    }
	}
	
	private void validateActive(
	        PatientContact contact) {

	    if (contact.getStatus() == ContactStatus.INACTIVE) {

	        throw new PatientContactInactiveException(
	                contact.getId().toString());
	    }
	}
	
	private void validateDuplicate(
	        UUID patientId,
	        ContactType type,
	        String value) {

	    if (repository.existsByPatientIdAndContactTypeAndContactValue(
	            patientId,
	            type,
	            value)) {

	        throw new DuplicatePatientContactException(
	                type.toString(),
	                value);
	    }
	}
	
	private void validateDuplicate(
	        PatientContact contact,
	        UpdatePatientContactRequest request) {

	    if (repository.existsByPatientIdAndContactTypeAndContactValueAndIdNot(

	            contact.getPatient().getId(),

	            request.getContactType(),

	            request.getContactValue(),

	            contact.getId())) {

	        throw new DuplicatePatientContactException(
	                request.getContactType().toString(),
	                request.getContactValue());
	    }
	}
	
	private void validateContactValue(
	        ContactType type,
	        String value) {
		
		switch (type) {

		case EMAIL -> validateEmail(value);

		case MOBILE,
		     HOME,
		     WORK,
		     WHATSAPP,
		     EMERGENCY -> validatePhone(value);

		}		
		
	}
	
	private void validateEmail(
	        String value) {
	
	    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
	    Pattern pattern = Pattern.compile(emailRegex);

		if (value == null || !pattern.matcher(value).matches()) {

		    throw new InvalidPatientContactException(
		            value);
		}
	}
	
	private void validatePhone(
	        String value) {
		
		if (!PHONE_PATTERN.matcher(value).matches()) {

		    throw new InvalidPatientContactException(value);
		}
		
	}
}

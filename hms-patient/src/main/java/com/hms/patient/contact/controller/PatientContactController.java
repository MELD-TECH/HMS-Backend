package com.hms.patient.contact.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hms.patient.contact.dto.request.CreatePatientContactRequest;
import com.hms.patient.contact.dto.request.UpdatePatientContactRequest;
import com.hms.patient.contact.dto.response.PatientContactResponse;
import com.hms.patient.contact.service.PatientContactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(
        name = "Patient Contacts",
        description = "Patient Contact Management APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patients/{patientId}/contacts")
public class PatientContactController {

	private final PatientContactService service;
	
	@Operation(
		    summary = "Create patient contact",
		    description = "Creates a new contact for an existing patient")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_CREATE')")
	public PatientContactResponse createContact(

	        @PathVariable
	        UUID patientId,

	        @Valid
	        @RequestBody
	        CreatePatientContactRequest request) {

	    return service.createContact(
	            patientId,
	            request);
	}

	@Operation(
		    summary = "List all patient contacts",
		    description = "List all the contacts for an existing patient")
	@GetMapping
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_VIEW')")
	public List<PatientContactResponse> getContacts(

	        @PathVariable
	        UUID patientId) {

	    return service.getContacts(patientId);
	}
	
	@Operation(
		    summary = "Get a patient contact",
		    description = "Fetch the contact for an existing patient")
	@GetMapping("/{contactId}")
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_VIEW')")
	public PatientContactResponse getContact(

	        @PathVariable
	        UUID patientId,

	        @PathVariable
	        UUID contactId) {

	    return service.getContact(
	            patientId,
	            contactId);
	}
	
	@Operation(
		    summary = "Update patient contact",
		    description = "Updates an existing contact for an existing patient")
	@PutMapping("/{contactId}")
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_UPDATE')")
	public PatientContactResponse update(

	        @PathVariable
	        UUID patientId,

	        @PathVariable
	        UUID contactId,

	        @Valid
	        @RequestBody
	        UpdatePatientContactRequest request) {

	    return service.updateContact(
	            patientId,
	            contactId,
	            request);
	}
	
	@Operation(
		    summary = "Delete patient contact",
		    description = "Deletes a contact for an existing patient")
	@DeleteMapping("/{contactId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_DELETE')")
	public void delete(

	        @PathVariable
	        UUID patientId,

	        @PathVariable
	        UUID contactId) {

	    service.deleteContact(
	            patientId,
	            contactId);
	}
	
	@Operation(
		    summary = "Verify patient contact",
		    description = "Verifies contact for an existing patient")
	@PatchMapping("/{contactId}/verify")
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_VERIFY')")
	public PatientContactResponse verify(

	        @PathVariable
	        UUID patientId,

	        @PathVariable
	        UUID contactId) {

	    return service.verifyContact(
	            patientId,
	            contactId);
	}
	
	@Operation(
		    summary = "Set primary patient contact",
		    description = "Sets a primary contact for an existing patient")
	@PatchMapping("/{contactId}/primary")
	@PreAuthorize("hasAuthority('PATIENT_CONTACT_PRIMARY')")
	public PatientContactResponse setPrimary(

	        @PathVariable
	        UUID patientId,

	        @PathVariable
	        UUID contactId) {

	    return service.setPrimaryContact(
	            patientId,
	            contactId);
	}
	
	
}

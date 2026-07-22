package com.hms.patient.contact.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.InvalidPatientContactException;
import com.hms.common.exception.PatientNotFoundException;
import com.hms.events.security.patient.contact.PatientContactCreatedEvent;
import com.hms.events.security.patient.contact.PatientContactDeletedEvent;
import com.hms.events.security.patient.contact.PatientContactUpdatedEvent;
import com.hms.events.security.patient.contact.PatientContactVerifiedEvent;
import com.hms.events.security.patient.contact.PatientContactViewedEvent;
import com.hms.events.security.patient.contact.PatientPrimaryContactChangedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.contact.dto.request.CreatePatientContactRequest;
import com.hms.patient.contact.dto.request.UpdatePatientContactRequest;
import com.hms.patient.contact.dto.response.PatientContactResponse;
import com.hms.patient.contact.entity.PatientContact;
import com.hms.patient.contact.lifecycle.PatientContactLifecycleManager;
import com.hms.patient.contact.mapper.PatientContactMapper;
import com.hms.patient.contact.repository.PatientContactRepository;
import com.hms.patient.contact.validation.PatientContactValidator;
import com.hms.patient.entity.Patient;
import com.hms.patient.repository.PatientRepository;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientContactService {

	private final PatientRepository patientRepository;

	private final PatientContactRepository contactRepository;

	private final PatientContactMapper mapper;

	private final PatientContactValidator validator;

	private final PatientContactLifecycleManager lifecycleManager;

	private final SecurityEventPublisher securityEventPublisher;
	
	@Transactional
	public PatientContactResponse createContact(
	        UUID patientId,
	        CreatePatientContactRequest request) {
		
		Patient patient = loadPatient(patientId);

		validator.validateCreate(patient, request);

		PatientContact contact =
		        mapper.toEntity(patient, request);

		lifecycleManager.initialize(contact);

		handlePrimaryContact(patient, contact);

		contact = contactRepository.save(contact);

		publishSecurityCreated(contact, currentUsername());

		return mapper.toResponse(contact);
	}
	
	@Transactional(readOnly = true)
	public List<PatientContactResponse> getContacts(
	        UUID patientId){
		
		loadPatient(patientId);

		return contactRepository
		        .findByPatientIdOrderByPrimaryContactDescCreatedAtAsc(patientId)
		        .stream()
		        .map(mapper::toResponse)
		        .toList();
	}
	
	@Transactional(readOnly = true)
	public PatientContactResponse getContact(
	        UUID patientId,
	        UUID contactId) {
		
		PatientContact contact =
		        loadContact(patientId, contactId);

		publishSecurityViewed(contact, currentUsername());
		
		return mapper.toResponse(contact);
	}
	
	@Transactional
	public PatientContactResponse updateContact(
	        UUID patientId,
	        UUID contactId,
	        UpdatePatientContactRequest request) {
		
		PatientContact contact =
		        loadContact(patientId, contactId);

		validator.validateUpdate(contact, request);

		lifecycleManager.update(contact, request);

		handlePrimaryContact(contact.getPatient(), contact);

		contact = contactRepository.save(contact);

		publishSecurityUpdated(contact, currentUsername());

		return mapper.toResponse(contact);
	}
	
	@Transactional
	public void deleteContact(
	        UUID patientId,
	        UUID contactId) {
		
		PatientContact contact =
		        loadContact(patientId, contactId);

		validator.validateDelete(contact);

		lifecycleManager.deactivate(contact);

		contactRepository.save(contact);

		publishSecurityDeleted(contact, currentUsername());
	}
	
	@Transactional
	public PatientContactResponse verifyContact(
	        UUID patientId,
	        UUID contactId) {
		
		PatientContact contact =
		        loadContact(patientId, contactId);

		validator.validateVerify(contact);

		lifecycleManager.verify(contact, currentUsername());

		contactRepository.save(contact);

		publishSecurityVerified(contact, currentUsername());

		return mapper.toResponse(contact);
	}
	
	@Transactional
	public PatientContactResponse setPrimaryContact(
	        UUID patientId,
	        UUID contactId) {
				
		PatientContact contact =
		        loadContact(patientId, contactId);

		validator.validateSetPrimary(contact);

		contactRepository
		        .findByPatientIdAndPrimaryContactTrue(patientId)
		        .ifPresent(existing -> {

		            lifecycleManager.removePrimary(existing);

		            contactRepository.save(existing);

		        });

		lifecycleManager.makePrimary(contact);

		contactRepository.save(contact);

		publishSecurityPrimaryChanged(contact, currentUsername());

		return mapper.toResponse(contact);
	}
	
	private Patient loadPatient(UUID patientId) {
		
		return patientRepository.findById(patientId)

		        .orElseThrow(() ->
		                new PatientNotFoundException(patientId));
	}
	
	private PatientContact loadContact(
	        UUID patientId,
	        UUID contactId) {
		
		return contactRepository
		        .findByIdAndPatientId(contactId, patientId)

		        .orElseThrow(() ->
		                new InvalidPatientContactException(contactId.toString()));
	}
	
	private void handlePrimaryContact(
	        Patient patient,
	        PatientContact contact) {
		
		if (!Boolean.TRUE.equals(contact.getPrimaryContact())) {
		    return;
		}

		contactRepository
		        .findByPatientIdAndPrimaryContactTrue(patient.getId())
		        .ifPresent(existing -> {

		            if (!existing.getId().equals(contact.getId())) {

		                lifecycleManager.removePrimary(existing);

		                contactRepository.save(existing);
		            }
		        });
	}
	
	private String currentUsername() {
	    return SecurityUtils.getCurrentUsername();
	}
	
	private void publishSecurityCreated(
	        PatientContact contact,
	        String username) {

	    securityEventPublisher.publish(

	            new PatientContactCreatedEvent(

	                    username,

	                    contact.getId().toString(),

	                    contact.getPatient().getPatientNumber(),

	                    contact.getContactType().name(),

	                    contact.getContactValue()));
	}
	
	private void publishSecurityUpdated(
	        PatientContact contact,
	        String username) {

	    securityEventPublisher.publish(

	            new PatientContactUpdatedEvent(

	                    username,

	                    contact.getId().toString(),

	                    contact.getPatient().getPatientNumber(),

	                    contact.getContactType().name()));
	}
	
	private void publishSecurityViewed(
	        PatientContact contact,
	        String username) {

	    securityEventPublisher.publish(

	            new PatientContactViewedEvent(

	                    username,

	                    contact.getId().toString(),

	                    contact.getPatient().getPatientNumber(),

	                    contact.getContactType().name()));
	}
	
	private void publishSecurityDeleted(
	        PatientContact contact,
	        String username) {

	    securityEventPublisher.publish(

	            new PatientContactDeletedEvent(

	                    username,

	                    contact.getId().toString(),

	                    contact.getPatient().getPatientNumber(),

	                    contact.getContactType().name()));
	}
	
	private void publishSecurityVerified(
	        PatientContact contact,
	        String username) {

	    securityEventPublisher.publish(

	            new PatientContactVerifiedEvent(

	                    username,

	                    contact.getId().toString(),

	                    contact.getPatient().getPatientNumber(),

	                    contact.getContactType().name()));
	}
	
	private void publishSecurityPrimaryChanged(
	        PatientContact contact,
	        String username) {

	    securityEventPublisher.publish(

	            new PatientPrimaryContactChangedEvent(

	                    username,

	                    contact.getId().toString(),

	                    contact.getPatient().getPatientNumber(),

	                    contact.getContactType().name()));
	}
	
	
}

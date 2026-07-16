package com.hms.api.patient.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.common.exception.DuplicatePatientEmailException;
import com.hms.common.exception.DuplicatePatientPhoneException;
import com.hms.common.exception.InvalidPatientDateOfBirthException;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
import com.hms.patient.repository.PatientRepository;
import com.hms.patient.validation.PatientRegistrationValidator;

@ExtendWith(MockitoExtension.class)
class PatientRegistrationValidatorTest {

	@Mock
	private PatientRepository repository;

	@InjectMocks
	private PatientRegistrationValidator validator;
	
	private CreatePatientRequest validRequest() {

	    return CreatePatientRequest.builder()
	            .firstName("John")
	            .middleName("Michael")
	            .lastName("Doe")
	            .dateOfBirth(LocalDate.of(1995,5,20))
	            .gender(Gender.MALE)
	            .maritalStatus(MaritalStatus.SINGLE)
	            .bloodGroup(BloodGroup.O_POSITIVE)
	            .genotype(Genotype.AA)
	            .email("john@test.com")
	            .phoneNumber("08012345678")
	            .build();
	}
	
	@Test
	void shouldAcceptValidRegistration() {

	    CreatePatientRequest request = validRequest();

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(false);

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(false);

	    assertDoesNotThrow(() ->
	            validator.validate(request));

	    verify(repository)
	            .existsByEmailIgnoreCase(request.getEmail());

	    verify(repository)
	            .existsByPhoneNumber(request.getPhoneNumber());
	    
	    verifyNoMoreInteractions(repository);
	}
	
	@Test
	void shouldRejectDuplicateEmail() {

	    CreatePatientRequest request = validRequest();

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(true);

	    assertThrows(
	    		
	    		DuplicatePatientEmailException.class,

	            () -> validator.validate(request));
	}
	
	@Test
	void shouldRejectDuplicatePhoneNumber() {

	    CreatePatientRequest request = validRequest();

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(false);

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(true);

	    assertThrows(

	            DuplicatePatientPhoneException.class,

	            () -> validator.validate(request));
	}
	
	@Test
	void shouldAllowNullEmail() {

	    CreatePatientRequest request = validRequest();

	    request.setEmail(null);

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(false);

	    assertDoesNotThrow(() ->
	            validator.validate(request));

	    verify(repository, never())
	            .existsByEmailIgnoreCase(any());
	}
	
	@Test
	void shouldAllowBlankEmail() {

	    CreatePatientRequest request = validRequest();

	    request.setEmail("");

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(false);

	    assertDoesNotThrow(() ->
	            validator.validate(request));

	    verify(repository, never())
	            .existsByEmailIgnoreCase(any());
	}
	
	@Test
	void shouldAllowNullPhone() {

	    CreatePatientRequest request = validRequest();

	    request.setPhoneNumber(null);

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(false);

	    assertDoesNotThrow(() ->
	            validator.validate(request));

	    verify(repository, never())
	            .existsByPhoneNumber(any());
	}
	
	@Test
	void shouldRejectFutureDateOfBirth() {

	    CreatePatientRequest request = validRequest();

	    request.setDateOfBirth(

	            LocalDate.now().plusDays(1));

	    assertThrows(

	            InvalidPatientDateOfBirthException.class,

	            () -> validator.validate(request));
	}
	
	@Test
	void shouldRejectNullRequest() {

	    assertThrows(

	            NullPointerException.class,

	            () -> validator.validate(null));
	}
	
	@Test
	void shouldSkipEmailValidationWhenEmailMissing() {

	    CreatePatientRequest request = validRequest();

	    request.setEmail(null);

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(false);

	    validator.validate(request);

	    verify(repository, never())
	            .existsByEmailIgnoreCase(any());
	}
	
	@Test
	void shouldSkipPhoneValidationWhenPhoneMissing() {

	    CreatePatientRequest request = validRequest();

	    request.setPhoneNumber(null);

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(false);

	    validator.validate(request);

	    verify(repository, never())
	            .existsByPhoneNumber(any());
	}
	
	@Test
	void shouldQueryRepositoryOnce() {

	    CreatePatientRequest request = validRequest();

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(false);

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(false);

	    validator.validate(request);

	    verify(repository, times(1))
	            .existsByEmailIgnoreCase(request.getEmail());

	    verify(repository, times(1))
	            .existsByPhoneNumber(request.getPhoneNumber());

	    verifyNoMoreInteractions(repository);
	}
	
	@Test
	void shouldValidateEmailIgnoringCase() {

	    CreatePatientRequest request = validRequest();

	    request.setEmail("John@Test.com");

	    when(repository.existsByEmailIgnoreCase("John@Test.com"))
	            .thenReturn(false);

	    when(repository.existsByPhoneNumber(request.getPhoneNumber()))
	            .thenReturn(false);

	    validator.validate(request);

	    verify(repository)
	            .existsByEmailIgnoreCase("John@Test.com");
	}
	
	@Test
	void shouldAllowBlankPhone() {

	    CreatePatientRequest request = validRequest();

	    request.setPhoneNumber("");

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(false);

	    assertDoesNotThrow(() ->
	            validator.validate(request));

	    verify(repository, never())
	            .existsByPhoneNumber(any());
	}
	
	@Test
	void shouldStopValidationAfterDuplicateEmail() {

	    CreatePatientRequest request = validRequest();

	    when(repository.existsByEmailIgnoreCase(request.getEmail()))
	            .thenReturn(true);

	    assertThrows(
	            DuplicatePatientEmailException.class,
	            () -> validator.validate(request));

	    verify(repository, never())
	            .existsByPhoneNumber(any());
	}
	
	@Test
	void shouldRejectFutureDateBeforeRepositoryLookup() {

	    CreatePatientRequest request = validRequest();

	    request.setDateOfBirth(LocalDate.now().plusDays(2));

	    assertThrows(
	            InvalidPatientDateOfBirthException.class,
	            () -> validator.validate(request));

	    verifyNoInteractions(repository);
	}
}
package com.hms.api.patient.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.mapper.PatientMapper;
import com.hms.patient.repository.PatientRepository;
import com.hms.patient.service.PatientNumberGenerator;
import com.hms.patient.service.impl.PatientServiceImpl;
import com.hms.patient.validation.PatientRegistrationValidator;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

	@Mock
	private PatientRepository repository;

	@Mock
	private PatientMapper mapper;

	@Mock
	private PatientNumberGenerator generator;

	@Mock
	private PatientRegistrationValidator validator;

	@Mock
	private SecurityEventPublisher publisher;

	@InjectMocks
	private PatientServiceImpl service;
	
	private CreatePatientRequest request;

	private Patient patient;

	private PatientResponse response;
	
	@BeforeEach
	void setup() {

	    request =
	            CreatePatientRequest.builder()

	                    .firstName("John")

	                    .lastName("Doe")

	                    .dateOfBirth(
	                            LocalDate.of(1990,1,1))

	                    .gender(Gender.MALE)

	                    .maritalStatus(
	                            MaritalStatus.SINGLE)

	                    .bloodGroup(
	                            BloodGroup.O_POSITIVE)

	                    .genotype(
	                            Genotype.AA)

	                    .phoneNumber("08012345678")

	                    .email("john@test.com")

	                    .build();

	    patient =
	            Patient.builder()

	                    .id(UUID.randomUUID())
	                    
	                    .patientNumber(
	                            "HMS-2026-000001")

	                    .firstName("John")

	                    .lastName("Doe")

	                    .status(PatientStatus.ACTIVE)

	                    .deceased(false)

	                    .build();

	    response =
	            PatientResponse.builder()

	                    .patientNumber(
	                            "HMS-2026-000001")

	                    .build();
	}
	
	@Test
	void shouldRegisterPatient() {

	    when(generator.generate())

	            .thenReturn("HMS-2026-000001");

	    when(mapper.toEntity(request))

	            .thenReturn(patient);

	    when(repository.save(any()))

	            .thenReturn(patient);

	    when(mapper.toResponse(patient))

	            .thenReturn(response);

	    PatientResponse result =
	            service.register(request);

	    assertEquals(

	            "HMS-2026-000001",

	            result.getPatientNumber());

	    verify(repository).save(any());

	}
	
	@Test
	void shouldGeneratePatientNumber() {

	    when(generator.generate())

	            .thenReturn("HMS-2026-000099");

	    when(mapper.toEntity(request))

	            .thenReturn(patient);

	    when(repository.save(any()))

	            .thenReturn(patient);

	    when(mapper.toResponse(any()))

	            .thenReturn(response);

	    service.register(request);

	    verify(generator)

	            .generate();
	}
	
	@Test
	void shouldValidateRegistration() {

	    when(generator.generate())

	            .thenReturn("HMS-2026-000001");

	    when(mapper.toEntity(request))

	            .thenReturn(patient);

	    when(repository.save(any()))

	            .thenReturn(patient);

	    when(mapper.toResponse(any()))

	            .thenReturn(response);

	    service.register(request);

	    verify(validator)

	            .validate(request);
	}
	
	@Test
	void shouldSavePatient() {

	    when(generator.generate())

	            .thenReturn("HMS-2026-000001");

	    when(mapper.toEntity(request))

	            .thenReturn(patient);

	    when(repository.save(any()))

	            .thenReturn(patient);

	    when(mapper.toResponse(any()))

	            .thenReturn(response);

	    service.register(request);

	    verify(repository)

	            .save(any(Patient.class));
	}
	
	@Test
	void shouldPublishPatientCreatedEvent() {

	    when(generator.generate())

	            .thenReturn("HMS-2026-000001");

	    when(mapper.toEntity(request))

	            .thenReturn(patient);

	    when(repository.save(any()))

	            .thenReturn(patient);

	    when(mapper.toResponse(any()))

	            .thenReturn(response);

	    service.register(request);

	    verify(publisher)

	            .publish(any());
	}
	
	@Test
	void shouldReturnPatientResponse() {

	    when(generator.generate())

	            .thenReturn("HMS-2026-000001");

	    when(mapper.toEntity(request))

	            .thenReturn(patient);

	    when(repository.save(any()))

	            .thenReturn(patient);

	    when(mapper.toResponse(any()))

	            .thenReturn(response);

	    PatientResponse result =
	            service.register(request);

	    assertNotNull(result);
	}
}

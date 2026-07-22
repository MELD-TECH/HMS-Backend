package com.hms.api.patient.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.hms.api.patient.support.PatientTestDataFactory;
import com.hms.api.patient.support.UniqueTestData;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.patient.dto.request.UpdatePatientRequest;
import com.hms.patient.entity.Patient;
import com.hms.patient.repository.PatientRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/003_roles.sql",
        "/db/testdata/004_role_permissions.sql",
        "/db/testdata/005_password_history.sql",
        "/db/testdata/006_security_admin.sql",
        "/db/testdata/007_mfa_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Transactional
class PatientUpdateIntegrationTest
        extends BaseIntegrationTest {

	@Autowired
	private PatientRepository repository;

	private Patient patient;
	
	@BeforeEach
	void setup() {

	    patient = repository.save(
	            PatientTestDataFactory.activePatient());
	}
	
	private ResultActions update(
	        UUID id,
	        UpdatePatientRequest request)
	        throws Exception {

	    return mockMvc.perform(

	            put("/api/v1/patients/{id}", id)

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainAdminToken())

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(request)));
	}
	
	
	private ResultActions updateWithoutAuthentication(
	        UUID id,
	        UpdatePatientRequest request)
	        throws Exception {

	    return mockMvc.perform(

	            put("/api/v1/patients/{id}", id)

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(request)));
	}
	
	
	private ResultActions updateWithSecurityAdmin(
	        UUID id,
	        UpdatePatientRequest request)
	        throws Exception {

	    return mockMvc.perform(

	            put("/api/v1/patients/{id}", id)

	                    .header(
	                            "Authorization",
	                            "Bearer "
	                                    + obtainSecurityAdminToken())

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(request)));
	}
	
	
	@Test
	void shouldUpdatePatient() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    update(patient.getId(), request)

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.id")
	                    .value(patient.getId().toString()))

	            .andExpect(jsonPath("$.firstName")
	                    .value(request.getFirstName()))

	            .andExpect(jsonPath("$.middleName")
	                    .value(request.getMiddleName()))

	            .andExpect(jsonPath("$.lastName")
	                    .value(request.getLastName()))

	            .andExpect(jsonPath("$.email")
	                    .value(request.getEmail()))

	            .andExpect(jsonPath("$.phoneNumber")
	                    .value(request.getPhoneNumber()))

	            .andExpect(jsonPath("$.maritalStatus")
	                    .value(request.getMaritalStatus().name()))

	            .andExpect(jsonPath("$.bloodGroup")
	                    .value(request.getBloodGroup().name()))

	            .andExpect(jsonPath("$.genotype")
	                    .value(request.getGenotype().name()));

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getFirstName())
	            .isEqualTo(request.getFirstName());

	    assertThat(updated.getEmail())
	            .isEqualTo(request.getEmail());

	    assertThat(updated.getPhoneNumber())
	            .isEqualTo(request.getPhoneNumber());
	}
	
	@Test
	void shouldUpdateFirstName() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setFirstName(
	            request.getFirstName());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getFirstName())
	            .isEqualTo(request.getFirstName());
	}
	
	@Test
	void shouldUpdateMiddleName() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setMiddleName(
	            request.getMiddleName());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getMiddleName())
	            .isEqualTo(request.getMiddleName());
	}
	
	@Test
	void shouldUpdateLastName() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setLastName(
	            request.getLastName());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getLastName())
	            .isEqualTo(request.getLastName());
	}
	
	@Test
	void shouldUpdateEmail() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setEmail(
	            UniqueTestData.email());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getEmail())
	            .isEqualTo(request.getEmail());
	}
	
	@Test
	void shouldUpdatePhoneNumber() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setPhoneNumber(
	            UniqueTestData.phoneNumber());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getPhoneNumber())
	            .isEqualTo(request.getPhoneNumber());
	}
	
	@Test
	void shouldUpdateDateOfBirth() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setDateOfBirth(
	            request.getDateOfBirth());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getDateOfBirth())
	            .isEqualTo(request.getDateOfBirth());
	}
	
	@Test
	void shouldUpdateGender() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setGender(
	            request.getGender());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getGender())
	            .isEqualTo(request.getGender());
	}
	
	@Test
	void shouldUpdateMaritalStatus() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setMaritalStatus(
	            request.getMaritalStatus());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getMaritalStatus())
	            .isEqualTo(request.getMaritalStatus());
	}
	
	@Test
	void shouldUpdateBloodGroup() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setBloodGroup(
	            request.getBloodGroup());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getBloodGroup())
	            .isEqualTo(request.getBloodGroup());
	}
	
	@Test
	void shouldUpdateGenotype() throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    request.setGenotype(
	            request.getGenotype());

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getGenotype())
	            .isEqualTo(request.getGenotype());
	}
	
	@Test
	void shouldRejectBlankFirstName() throws Exception {

	    update(
	            patient.getId(),
	            PatientTestDataFactory.blankFirstNameRequest(patient))

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectBlankLastName() throws Exception {

	    update(
	            patient.getId(),
	            PatientTestDataFactory.blankLastNameRequest(patient))

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectInvalidEmail() throws Exception {

	    update(
	            patient.getId(),
	            PatientTestDataFactory.invalidEmailRequest(patient))

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectInvalidPhoneNumber() throws Exception {

	    update(
	            patient.getId(),
	            PatientTestDataFactory.invalidPhoneRequest(patient))

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectFutureDateOfBirth() throws Exception {

	    update(
	            patient.getId(),
	            PatientTestDataFactory.futureDobRequest(patient))

	            .andExpect(status().isBadRequest());
	}
	

	@Test
	void shouldRejectDuplicateEmail() throws Exception {

	    Patient another =
	            repository.save(
	                    PatientTestDataFactory.secondPatient());

	    update(

	            patient.getId(),

	            PatientTestDataFactory
	                    .duplicateEmailRequest(
	                            another.getEmail(), patient))

	            .andExpect(status().isConflict());
	}

	@Test
	void shouldRejectDuplicatePhoneNumber() throws Exception {

	    Patient another =
	            repository.save(
	                    PatientTestDataFactory.secondPatient());

	    update(

	            patient.getId(),

	            PatientTestDataFactory
	                    .duplicatePhoneRequest(
	                            another.getPhoneNumber(), patient))

	            .andExpect(status().isConflict());
	}
	
	@Test
	void shouldReturnNotFoundWhenPatientDoesNotExist()
	        throws Exception {

	    update(

	            UUID.randomUUID(),

	            PatientTestDataFactory.validUpdateRequest(patient))

	            .andExpect(status().isNotFound());
	}
	
	@Test
	void shouldRejectWithoutAuthentication()
	        throws Exception {

	    updateWithoutAuthentication(

	            patient.getId(),

	            PatientTestDataFactory.validUpdateRequest(patient))

	            .andExpect(status().isUnauthorized());
	}
	
	@Test
	void shouldRejectWithoutPermission()
	        throws Exception {

	    updateWithSecurityAdmin(

	            patient.getId(),

	            PatientTestDataFactory.validUpdateRequest(patient))

	            .andExpect(status().isForbidden());
	}
	
	@Test
	void shouldRejectUpdatingArchivedPatient()
	        throws Exception {

	    Patient archived =
	            repository.save(
	                    PatientTestDataFactory.archivedPatient());

	    update(

	            archived.getId(),

	            PatientTestDataFactory.validUpdateRequest(patient))

	            .andExpect(status().isConflict());
	}
	
	@Test
	void shouldRejectUpdatingDeceasedPatient()
	        throws Exception {

	    Patient deceased =
	            repository.save(
	                    PatientTestDataFactory.deceasedPatient());

	    update(

	            deceased.getId(),

	            PatientTestDataFactory.validUpdateRequest(patient))

	            .andExpect(status().isConflict());
	}
	
	
	@Test
	void shouldUpdateAuditInformation()
	        throws Exception {

	    UpdatePatientRequest request =
	            PatientTestDataFactory.validUpdateRequest(patient);

	    update(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient updated =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(updated.getUpdatedAt()).isNotNull();

	    assertThat(updated.getCreatedBy())
        .isEqualTo(patient.getCreatedBy());
	    
	    assertThat(updated.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(updated.getCreatedAt())
        .isNotNull();
	    
	    assertThat(updated.getUpdatedAt())
        .isAfter(updated.getCreatedAt());

	}	
	
}

package com.hms.api.patient.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.hms.api.patient.support.PatientTestDataFactory;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.events.security.patient.PatientRestoredEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.RestorePatientRequest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.repository.PatientRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

@Transactional

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
class PatientRestoreIntegrationTest
        extends BaseIntegrationTest {
	
	@Autowired
	private PatientRepository repository;
	
	@MockitoBean
	private SecurityEventPublisher publisher;
	
	private Patient patient;
	
	@BeforeEach
	void setup() {

	    patient =

	            repository.save(

	                    PatientTestDataFactory.archivedPatient());
	}
	
	protected ResultActions restore(

	        UUID patientId,

	        RestorePatientRequest request)

	        throws Exception {

	    return performAuthenticatedRequest(

	            HttpMethod.PATCH,

	            "/api/v1/patients/" + patientId + "/restore",

	            request);
	}
	
	protected ResultActions restoreWithoutAuthentication(

	        UUID patientId,

	        RestorePatientRequest request)

	        throws Exception {

	    return mockMvc.perform(

	            patch("/api/v1/patients/{id}/restore",

	                    patientId)

	                    .contentType(

	                            MediaType.APPLICATION_JSON)

	                    .content(

	                            objectMapper.writeValueAsString(

	                                    request)));
	}
	
	protected ResultActions restoreAsSecurityAdmin(

	        UUID patientId,

	        RestorePatientRequest request)

	        throws Exception {

	    return mockMvc.perform(

	            patch("/api/v1/patients/{id}/restore",

	                    patientId)

	                    .header(

	                            HttpHeaders.AUTHORIZATION,

	                            bearerSecurityAdminToken())

	                    .contentType(

	                            MediaType.APPLICATION_JSON)

	                    .content(

	                            objectMapper.writeValueAsString(

	                                    request)));
	}
	
	private Patient reloadPatient() {

	    return repository

	            .findById(patient.getId())

	            .orElseThrow();
	}
	
	private void assertRestored(

	        Patient patient) {

	    assertThat(patient.getStatus())

	            .isEqualTo(PatientStatus.ACTIVE);

	    assertThat(patient.getArchived())

	            .isFalse();

	    assertThat(patient.getArchiveReason())

	            .isNull();

	    assertThat(patient.getArchivedBy())

	            .isNull();

	    assertThat(patient.getArchivedAt())

	            .isNull();
	}
	
	private void assertNotRestored(

	        Patient patient) {

	    assertThat(patient.getStatus())

	            .isEqualTo(PatientStatus.ARCHIVED);

	    assertThat(patient.getArchived())

	            .isTrue();

	    assertThat(patient.getArchiveReason())

	            .isNotNull();

	    assertThat(patient.getArchivedBy())

	            .isNotNull();

	    assertThat(patient.getArchivedAt())

	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should restore archived patient")
	void shouldRestorePatient()
	        throws Exception {

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .validRestoreRequest();

	    restore(

	            patient.getId(),

	            request)

	            .andExpect(

	                    status().isOk());

	    Patient restored =

	            reloadPatient();

	    assertRestored(

	            restored);
	}
	
	@Test
	@DisplayName("Should return 404 when patient does not exist")
	void shouldReturnNotFoundWhenPatientDoesNotExist()
	        throws Exception {

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .validRestoreRequest();

	    restore(

	            UUID.randomUUID(),

	            request)

	            .andExpect(

	                    status().isNotFound());
	}
	
	@Test
	@DisplayName("Should reject blank restore reason")
	void shouldRejectBlankRestoreReason()
	        throws Exception {

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .blankRestoreRequest();

	    restore(

	            patient.getId(),

	            request)

	            .andExpect(

	                    status().isBadRequest());

	    assertNotRestored(

	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should reject null restore reason")
	void shouldRejectNullRestoreReason()
	        throws Exception {

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .nullRestoreRequest();

	    restore(

	            patient.getId(),

	            request)

	            .andExpect(

	                    status().isBadRequest());

	    assertNotRestored(

	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should reject restore reason exceeding maximum length")
	void shouldRejectRestoreReasonTooLong()
	        throws Exception {

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .longRestoreRequest();

	    restore(

	            patient.getId(),

	            request)

	            .andExpect(

	                    status().isBadRequest());

	    assertNotRestored(

	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should reject restoring active patient")
	void shouldRejectActivePatient()
	        throws Exception {

	    Patient active =

	            repository.save(

	                    PatientTestDataFactory.activePatient());

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .validRestoreRequest();

	    restore(

	            active.getId(),

	            request)

	            .andExpect(

	                    status().isConflict());

	    assertThat(

	            repository.findById(active.getId())

	                    .orElseThrow()

	                    .getStatus())

	            .isEqualTo(

	                    PatientStatus.ACTIVE);
	}
	
	@Test
	@DisplayName("Should reject restoring deceased patient")
	void shouldRejectDeceasedPatient()
	        throws Exception {

	    Patient deceased =

	            repository.save(

	                    PatientTestDataFactory.deceasedPatient());

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .validRestoreRequest();

	    restore(

	            deceased.getId(),

	            request)

	            .andExpect(

	                    status().isConflict());

	    assertThat(

	            repository.findById(

	                    deceased.getId())

	                    .orElseThrow()

	                    .getStatus())

	            .isEqualTo(

	                    PatientStatus.DECEASED);
	}
	

	@Test
	@DisplayName("Should reject restoring already restored patient")
	void shouldRejectSecondRestore()
	        throws Exception {

	    RestorePatientRequest request =

	            PatientTestDataFactory

	                    .validRestoreRequest();

	    restore(

	            patient.getId(),

	            request)

	            .andExpect(

	                    status().isOk());

	    restore(

	            patient.getId(),

	            request)

	            .andExpect(

	                    status().isConflict());

	    assertRestored(

	            reloadPatient());
	}
	
	
	@Test
	@DisplayName("Should update audit information when patient is restored")
	void shouldUpdateAuditInformation()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient restored =
	            reloadPatient();

	    assertThat(restored.getCreatedBy())
	            .isEqualTo(patient.getCreatedBy());

	    assertThat(restored.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(restored.getCreatedAt())
	            .isNotNull();

	    assertThat(restored.getUpdatedAt())
	            .isAfter(restored.getCreatedAt());
	}
	
	@Test
	@DisplayName("Should preserve creation audit information")
	void shouldPreserveCreationAuditInformation()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient restored =
	            reloadPatient();

	    assertThat(restored.getCreatedBy())
	            .isEqualTo(patient.getCreatedBy());

	    assertThat(restored.getCreatedAt())
	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should reject restore without authentication")
	void shouldRejectWithoutAuthentication()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restoreWithoutAuthentication(
	            patient.getId(),
	            request)

	            .andExpect(status().isUnauthorized());

	    assertNotRestored(
	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should reject restore without required permission")
	void shouldRejectWithoutPermission()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restoreAsSecurityAdmin(
	            patient.getId(),
	            request)

	            .andExpect(status().isForbidden());

	    assertNotRestored(
	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should persist restored patient")
	void shouldPersistRestoredPatient()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient restored =
	            reloadPatient();

	    assertThat(restored.getStatus())
	            .isEqualTo(PatientStatus.ACTIVE);

	    assertThat(restored.getArchived())
	            .isFalse();

	    assertThat(restored.getArchiveReason())
	            .isNull();

	    assertThat(restored.getArchivedBy())
	            .isNull();

	    assertThat(restored.getArchivedAt())
	            .isNull();
	}
	
	@Test
	@DisplayName("Should preserve patient identity after restore")
	void shouldPreservePatientIdentity()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient restored =
	            reloadPatient();

	    assertThat(restored.getPatientNumber())
	            .isEqualTo(patient.getPatientNumber());

	    assertThat(restored.getFirstName())
	            .isEqualTo(patient.getFirstName());

	    assertThat(restored.getLastName())
	            .isEqualTo(patient.getLastName());

	    assertThat(restored.getEmail())
	            .isEqualTo(patient.getEmail());

	    assertThat(restored.getPhoneNumber())
	            .isEqualTo(patient.getPhoneNumber());
	}
	
	@Test
	@DisplayName("Should preserve deceased information when restoring archived patient")
	void shouldPreserveDeceasedFields()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient restored =
	            reloadPatient();

	    assertThat(restored.getDeceased())
	            .isFalse();

	    assertThat(restored.getDeceasedDate())
	            .isNull();

	    assertThat(restored.getCauseOfDeath())
	            .isNull();

	    assertThat(restored.getDeceasedNotes())
	            .isNull();
	}
	
	@Test
	@DisplayName("Should return restored patient response")
	void shouldReturnRestoredPatientResponse()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory
	                    .validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.id")
	                    .value(patient.getId().toString()))

	            .andExpect(jsonPath("$.status")
	                    .value("ACTIVE"))

	            .andExpect(jsonPath("$.archived")
	                    .value(false))

	            .andExpect(jsonPath("$.archiveReason")
	                    .doesNotExist());
	}
	
	@Test
	@DisplayName("Should publish PatientRestoredEvent")
	void shouldPublishPatientRestoredEvent()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory.validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    verify(
	            publisher,
	            times(1))

	            .publish(any(PatientRestoredEvent.class));
	}
	
	@Test
	@DisplayName("Should publish PatientRestoredEvent with correct values")
	void shouldPublishRestoreEventWithCorrectValues()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory.validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    ArgumentCaptor<PatientRestoredEvent> captor =
	            ArgumentCaptor.forClass(
	                    PatientRestoredEvent.class);

	    verify(publisher)
	            .publish(captor.capture());

	    PatientRestoredEvent event =
	            captor.getValue();

	    assertThat(event.getUsername())
	            .isEqualTo("admin");

	    assertThat(event.getEntityId())
	            .isEqualTo(patient.getId().toString());

	    assertThat(event.getPatientNumber())
	            .isEqualTo(patient.getPatientNumber());

	    assertThat(event.getReason())
	            .isEqualTo(request.getReason());
	}
	
	@Test
	@DisplayName("Should publish restore event exactly once")
	void shouldPublishRestoreEventOnlyOnce()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory.validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    verify(
	            publisher,
	            times(1))

	            .publish(any(PatientRestoredEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event when validation fails")
	void shouldNotPublishRestoreEventWhenValidationFails()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory.blankRestoreRequest();

	    String token = bearerToken();
	    
	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/restore", patient.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());

	    verifyNoInteractions(publisher);
	}
	
	@Test
	@DisplayName("Should not publish event for active patient")
	void shouldNotPublishEventForActivePatient()
	        throws Exception {

	    Patient active =
	            repository.save(
	                    PatientTestDataFactory.activePatient());

	    RestorePatientRequest request =
	            PatientTestDataFactory.validRestoreRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/restore", active.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict());

	    verifyNoInteractions(publisher);
	}
	
	@Test
	@DisplayName("Should not publish event on second restore")
	void shouldNotPublishSecondRestoreEvent()
	        throws Exception {

	    RestorePatientRequest request =
	            PatientTestDataFactory.validRestoreRequest();

	    restore(patient.getId(), request)

	            .andExpect(status().isOk());

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/restore", patient.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict());

	    verifyNoInteractions(publisher);
	}
	
	
}

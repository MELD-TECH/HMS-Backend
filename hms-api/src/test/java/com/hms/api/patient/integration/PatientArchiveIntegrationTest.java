package com.hms.api.patient.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.hms.api.patient.support.PatientTestDataFactory;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.events.security.patient.PatientArchivedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.ArchivePatientRequest;
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

class PatientArchiveIntegrationTest
        extends BaseIntegrationTest {

	@Autowired
	private PatientRepository repository;

	private Patient patient;
	
	@MockitoBean
	private SecurityEventPublisher publisher;
	
	@BeforeEach
	void setup() {

	    patient =
	            repository.save(
	                    PatientTestDataFactory.activePatient());
	}
	
	protected ResultActions performPatientAction(

	        HttpMethod method,

	        String uri,

	        Object request)

	        throws Exception {

	    return mockMvc.perform(

	            MockMvcRequestBuilders.request(
	                    method,
	                    uri)

	                    .header(
	                            HttpHeaders.AUTHORIZATION,
	                            bearerToken())

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(
	                                    request)));
	}
	
	protected String bearerToken() throws Exception{

	    return "Bearer " + obtainAdminToken();
	}
	
	protected String bearerSecurityAdminToken() throws Exception{

	    return "Bearer " + obtainSecurityAdminToken();
	}
	
	
	protected ResultActions archive(

	        UUID patientId,

	        ArchivePatientRequest request)

	        throws Exception {

	    return performPatientAction(

	            HttpMethod.PATCH,

	            "/api/v1/patients/" + patientId + "/archive",

	            request);
	}
	
	protected ResultActions archiveWithoutAuthentication(

	        UUID patientId,

	        ArchivePatientRequest request)

	        throws Exception {

	    return mockMvc.perform(

	            patch("/api/v1/patients/{id}/archive",
	                    patientId)

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(
	                                    request)));
	}
	
	protected ResultActions archiveAsSecurityAdmin(

	        UUID patientId,

	        ArchivePatientRequest request)

	        throws Exception {

	    return mockMvc.perform(

	            patch("/api/v1/patients/{id}/archive",
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
	
	private void assertArchived(

	        Patient patient,

	        String reason) {

	    assertThat(patient.getStatus())
	            .isEqualTo(PatientStatus.ARCHIVED);

	    assertThat(patient.getArchiveReason())
	            .isEqualTo(reason);

	    assertThat(patient.getArchivedAt())
	            .isNotNull();

	    assertThat(patient.getArchivedBy())
	            .isEqualTo("admin");

	    assertThat(patient.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(patient.getUpdatedAt())
	            .isAfter(patient.getCreatedAt());
	}
	
	private Patient reloadPatient() {

	    return repository
	            .findById(patient.getId())
	            .orElseThrow();
	}
	
	private void assertNotArchived(
	        Patient patient) {

	    assertThat(patient.getStatus())
	            .isEqualTo(PatientStatus.ACTIVE);

	    assertThat(patient.getArchiveReason())
	            .isNull();

	    assertThat(patient.getArchivedAt())
	            .isNull();

	    assertThat(patient.getArchivedBy())
	            .isNull();
	}
	
	
	@Test
	@DisplayName("Should archive an active patient")
	void shouldArchivePatient()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient archived =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertArchived(
	            archived,
	            request.getReason());
	}
	
	@Test
	@DisplayName("Should return 404 when patient does not exist")
	void shouldReturnNotFoundWhenPatientDoesNotExist()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(
	            UUID.randomUUID(),
	            request)

	            .andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should reject blank archive reason")
	void shouldRejectBlankArchiveReason()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .blankArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isBadRequest());

	    Patient persisted = reloadPatient();

	    assertThat(persisted.getStatus())
	            .isEqualTo(PatientStatus.ACTIVE);
	}
	
	@Test
	@DisplayName("Should reject null archive reason")
	void shouldRejectNullArchiveReason()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .nullArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isBadRequest());

	    Patient persisted = reloadPatient();

	    assertThat(persisted.getStatus())
	            .isEqualTo(PatientStatus.ACTIVE);
	    
	    assertNotArchived(
	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should reject archive reason exceeding maximum length")
	void shouldRejectArchiveReasonTooLong()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .longArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isBadRequest());

	    Patient persisted = reloadPatient();

	    assertThat(persisted.getStatus())
	            .isEqualTo(PatientStatus.ACTIVE);
	}
	
	@Test
	@DisplayName("Should update audit information when patient is archived")
	void shouldUpdateAuditInformation()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient archived =
	            repository.findById(patient.getId())
	                    .orElseThrow();

	    assertThat(archived.getCreatedBy())
	            .isEqualTo(patient.getCreatedBy());

	    assertThat(archived.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(archived.getArchivedBy())
	            .isEqualTo("admin");

	    assertThat(
	            archived.getCreatedAt())
	                   .isNotNull();

	    assertThat(archived.getUpdatedAt())
	            .isAfter(archived.getCreatedAt());

	    assertThat(archived.getArchivedAt())
        .isNotNull();
	}
	
	@Test
	@DisplayName("Should reject archive without authentication")
	void shouldRejectWithoutAuthentication()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archiveWithoutAuthentication(
	            patient.getId(),
	            request)

	            .andExpect(status().isUnauthorized());

	    assertNotArchived(
	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should reject archive without required permission")
	void shouldRejectWithoutPermission()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archiveAsSecurityAdmin(
	            patient.getId(),
	            request)

	            .andExpect(status().isForbidden());

	    assertNotArchived(
	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should persist archive information")
	void shouldPersistArchiveInformation()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient archived =
	            reloadPatient();

	    assertThat(archived.getStatus())
	            .isEqualTo(PatientStatus.ARCHIVED);

	    assertThat(archived.getArchiveReason())
	            .isEqualTo(request.getReason());

	    assertThat(archived.getArchivedAt())
	            .isNotNull();

	    assertThat(archived.getArchivedBy())
	            .isEqualTo("admin");

	    assertThat(archived.getUpdatedBy())
	            .isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Should reject already archived patient")
	void shouldRejectAlreadyArchivedPatient()
	        throws Exception {

	    Patient archivedPatient =
	            repository.save(
	                    PatientTestDataFactory
	                            .archivedPatient());

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(
	            archivedPatient.getId(),
	            request)

	            .andExpect(status().isConflict());

	    Patient persisted =
	            repository.findById(
	                    archivedPatient.getId())
	                    .orElseThrow();

	    assertThat(persisted.getStatus())
	            .isEqualTo(PatientStatus.ARCHIVED);
	}
	
	@Test
	@DisplayName("Should reject deceased patient")
	void shouldRejectArchivingDeceasedPatient()
	        throws Exception {

	    Patient deceased =
	            repository.save(
	                    PatientTestDataFactory
	                            .deceasedPatient());

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(
	            deceased.getId(),
	            request)

	            .andExpect(status().isConflict());

	    Patient persisted =
	            repository.findById(
	                    deceased.getId())
	                    .orElseThrow();

	    assertThat(persisted.getStatus())
	            .isEqualTo(PatientStatus.DECEASED);
	}
	
	@Test
	@DisplayName("Should reject second archive request")
	void shouldRejectSecondArchive()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory
	                    .validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    archive(patient.getId(), request)

	            .andExpect(status().isConflict());

	    Patient archived =
	            reloadPatient();

	    assertThat(archived.getStatus())
	            .isEqualTo(PatientStatus.ARCHIVED);

	    assertThat(archived.getArchiveReason())
	            .isEqualTo(request.getReason());
	}
	
	
	@Test
	@DisplayName("Should publish PatientArchivedEvent")
	void shouldPublishPatientArchivedEvent()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory.validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    verify(
	            publisher,
	            times(1))

	            .publish(any(PatientArchivedEvent.class));
	}
	
	@Test
	@DisplayName("Should publish PatientArchivedEvent with correct values")
	void shouldPublishArchivedEventWithCorrectValues()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory.validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    ArgumentCaptor<PatientArchivedEvent> captor =
	            ArgumentCaptor.forClass(
	                    PatientArchivedEvent.class);

	    verify(publisher)
	            .publish(captor.capture());

	    PatientArchivedEvent event =
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
	@DisplayName("Should return archived patient")
	void shouldReturnArchivedPatientResponse()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory.validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.id")
	                    .value(patient.getId().toString()))

	            .andExpect(jsonPath("$.status")
	                    .value("ARCHIVED"))

	            .andExpect(jsonPath("$.archiveReason")
	                    .value(request.getReason()));
	}
	
	@Test
	@DisplayName("Should persist archived patient")
	void shouldPersistArchivedPatient()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory.validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    Patient archived =
	            reloadPatient();

	    assertArchived(
	            archived,
	            request.getReason());

	    assertThat(archived.getDeceased())
	            .isFalse();

	    assertThat(archived.getPatientNumber())
	            .isEqualTo(patient.getPatientNumber());

	    assertThat(archived.getArchiveReason())
	            .isEqualTo(request.getReason());
	}
	
	@Test
	@DisplayName("Should publish archive event exactly once")
	void shouldPublishArchiveEventOnlyOnce()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory.validArchiveRequest();

	    archive(patient.getId(), request)

	            .andExpect(status().isOk());

	    verify(
	            publisher,
	            times(1))

	            .publish(any(PatientArchivedEvent.class));
	}
	
	@Test
	void shouldNotPublishArchiveEventWhenValidationFails()
	        throws Exception {

	    ArchivePatientRequest request =
	            PatientTestDataFactory.blankArchiveRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/archive", patient.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());

	    verifyNoInteractions(publisher);
	}
	
	@Test
	void shouldNotPublishEventForAlreadyArchivedPatient()
	        throws Exception {

	    Patient archived =
	            repository.save(
	                    PatientTestDataFactory.archivedPatient());

	    ArchivePatientRequest request =
	            PatientTestDataFactory.validArchiveRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/archive", archived.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict());

	    verifyNoInteractions(publisher);
	}
	
	
}

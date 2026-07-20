package com.hms.api.patient.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import static org.mockito.Mockito.*;

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
import com.hms.events.security.patient.PatientActivatedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.ActivatePatientRequest;
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
class PatientActivateIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PatientRepository repository;

    private Patient patient;
    
	@MockitoBean
	private SecurityEventPublisher publisher;

    @BeforeEach
    void setup() {

        patient = repository.save(
                PatientTestDataFactory.archivedPatient());
    }

    protected ResultActions activate(
            UUID patientId,
            ActivatePatientRequest request)
            throws Exception {

        return performAuthenticatedRequest(
                HttpMethod.PATCH,
                "/api/v1/patients/" + patientId + "/activate",
                request);
    }
    
    protected ResultActions activateWithoutAuthentication(
            UUID patientId,
            ActivatePatientRequest request)
            throws Exception {

        return performWithoutAuthentication(
                HttpMethod.PATCH,
                "/api/v1/patients/" + patientId + "/activate",
                request);
    }
    
    protected ResultActions activateAsSecurityAdmin(
            UUID patientId,
            ActivatePatientRequest request)
            throws Exception {

        return performAsSecurityAdmin(
                HttpMethod.PATCH,
                "/api/v1/patients/" + patientId + "/activate",
                request);
    }
    
    private Patient reloadPatient() {

        return reload(
                repository,
                patient.getId());
    }
    
    private void assertActivated(
            Patient patient) {

        assertThat(patient.getStatus())
                .isEqualTo(PatientStatus.ACTIVE);

        assertThat(patient.getUpdatedBy())
                .isEqualTo("admin");

        assertThat(patient.getUpdatedAt())
                .isNotNull();
    }
    
    private void assertNotActivated(
            Patient patient) {

        assertThat(patient.getStatus())
                .isEqualTo(PatientStatus.ARCHIVED);
    }
    
    @Test
    @DisplayName("Should activate archived patient")
    void shouldActivateArchivedPatient()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        Patient activated =
                reloadPatient();

        assertActivated(activated);
    }
    
    @Test
    @DisplayName("Should return 404 when patient does not exist")
    void shouldReturnNotFoundWhenPatientDoesNotExist()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(
                UUID.randomUUID(),
                request)

                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should reject blank activation reason")
    void shouldRejectBlankReason()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.blankActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isBadRequest());

        assertNotActivated(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject null activation reason")
    void shouldRejectNullReason()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.nullActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isBadRequest());

        assertNotActivated(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject long activation reason")
    void shouldRejectLongReason()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.longActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isBadRequest());

        assertNotActivated(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject already active patient")
    void shouldRejectAlreadyActivePatient()
            throws Exception {

        Patient active =
                repository.save(
                        PatientTestDataFactory.activePatient());

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(
                active.getId(),
                request)

                .andExpect(status().isConflict());

        assertThat(
                repository.findById(active.getId())
                        .orElseThrow()
                        .getStatus())

                .isEqualTo(PatientStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Should reject deceased patient")
    void shouldRejectDeceasedPatient()
            throws Exception {

        Patient deceased =
                repository.save(
                        PatientTestDataFactory.deceasedPatient());

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(
                deceased.getId(),
                request)

                .andExpect(status().isConflict());

        assertThat(
                repository.findById(deceased.getId())
                        .orElseThrow()
                        .getStatus())

                .isEqualTo(PatientStatus.DECEASED);
    }
    
    @Test
    @DisplayName("Should reject second activation")
    void shouldRejectSecondActivation()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        activate(patient.getId(), request)

                .andExpect(status().isConflict());

        Patient activated =
                reloadPatient();

        assertActivated(activated);
    }
    
    @Test
    @DisplayName("Should update audit information when patient is activated")
    void shouldUpdateAuditInformation()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        Patient before =
                reloadPatient();

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        Patient activated =
                reloadPatient();

        assertThat(activated.getUpdatedAt())
                .isNotNull();

        assertThat(activated.getUpdatedBy())
                .isEqualTo("admin");

        assertThat(activated.getCreatedAt())
                .isNotNull();

        assertThat(activated.getCreatedBy())
                .isEqualTo(before.getCreatedBy());

        assertThat(activated.getUpdatedAt())
                .isNotNull();
    }
    
    @Test
    @DisplayName("Should reject activation without authentication")
    void shouldRejectWithoutAuthentication()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activateWithoutAuthentication(
                patient.getId(),
                request)

                .andExpect(status().isUnauthorized());

        assertNotActivated(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject activation without permission")
    void shouldRejectWithoutPermission()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activateAsSecurityAdmin(
                patient.getId(),
                request)

                .andExpect(status().isForbidden());

        assertNotActivated(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should persist activated patient")
    void shouldPersistActivatedPatient()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        Patient activated =
                repository.findById(patient.getId())
                        .orElseThrow();

        assertThat(activated.getStatus())
                .isEqualTo(PatientStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Should persist activation information")
    void shouldPersistActivationInformation()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        Patient activated =
                reloadPatient();

        assertThat(activated.getStatus())
                .isEqualTo(PatientStatus.ACTIVE);

        assertThat(activated.getUpdatedBy())
                .isEqualTo("admin");

        assertThat(activated.getUpdatedAt())
                .isNotNull();

        /*
         * Verify these only if activation clears archive metadata.
         */
        assertThat(activated.getArchivedAt()).isNull();
        assertThat(activated.getArchivedBy()).isNull();
        assertThat(activated.getArchiveReason()).isNull();
    }
    
    @Test
    @DisplayName("Should publish patient activated event")
    void shouldPublishPatientActivatedEvent()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        reset(publisher);

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        verify(publisher)
                .publish(any(PatientActivatedEvent.class));
    }
    
    @Test
    @DisplayName("Should publish activated event with correct values")
    void shouldPublishActivatedEventWithCorrectValues()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        reset(publisher);

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        ArgumentCaptor<PatientActivatedEvent> captor =
                ArgumentCaptor.forClass(
                        PatientActivatedEvent.class);

        verify(publisher)
                .publish(captor.capture());

        PatientActivatedEvent event =
                captor.getValue();

        assertThat(event.getEntityId())
                .isEqualTo(patient.getId().toString());

        assertThat(event.getPatientNumber())
                .isEqualTo(patient.getPatientNumber());

        assertThat(event.getUsername())
                .isEqualTo("admin");

        assertThat(event.getReason())
                .isEqualTo(request.getReason());
    }
    
    @Test
    @DisplayName("Should return activated patient response")
    void shouldReturnActivatedPatient()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        activate(patient.getId(), request)

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(patient.getId().toString()))

                .andExpect(jsonPath("$.patientNumber")
                        .value(patient.getPatientNumber()))

                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));
    }
    
    @Test
    @DisplayName("Should publish activation event only once")
    void shouldPublishActivationEventOnlyOnce()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

        reset(publisher);

        activate(patient.getId(), request)

                .andExpect(status().isOk());

        verify(publisher, times(1))
                .publish(any(PatientActivatedEvent.class));
    }
    
    @Test
    @DisplayName("Should not publish event when validation fails")
    void shouldNotPublishEventWhenValidationFails()
            throws Exception {

        ActivatePatientRequest request =
                PatientTestDataFactory.blankActivateRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/activate", patient.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(asJson(request)))
	            .andExpect(status().isBadRequest());

	    verifyNoInteractions(publisher);
    }
    
    @Test
    @DisplayName("Should not publish event when patient is already active")
    void shouldNotPublishEventWhenAlreadyActive()
            throws Exception {

        Patient active =
                repository.save(
                        PatientTestDataFactory.activePatient());

        ActivatePatientRequest request =
                PatientTestDataFactory.validActivateRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/activate", active.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(asJson(request)))
	            .andExpect(status().isConflict());

	    verifyNoInteractions(publisher);
    }       
        
}

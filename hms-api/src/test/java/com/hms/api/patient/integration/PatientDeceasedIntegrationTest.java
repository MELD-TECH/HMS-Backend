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
import com.hms.events.security.patient.PatientDeceasedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.dto.request.ActivatePatientRequest;
import com.hms.patient.dto.request.DeceasedPatientRequest;
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
public class PatientDeceasedIntegrationTest extends BaseIntegrationTest  {

    @Autowired
    private PatientRepository repository;

    private Patient patient;
    
	@MockitoBean
	private SecurityEventPublisher publisher;
	
    @BeforeEach
    void setup() {

        patient = repository.save(
                PatientTestDataFactory.activePatient());
    }
    
    protected ResultActions deceased(
            UUID patientId,
            DeceasedPatientRequest request)
            throws Exception {

        return performAuthenticatedRequest(
                HttpMethod.PATCH,
                "/api/v1/patients/" + patientId + "/deceased",
                request);
    }
    
    protected ResultActions deceasedWithoutAuthentication(
            UUID patientId,
            DeceasedPatientRequest request)
            throws Exception {

        return performWithoutAuthentication(
                HttpMethod.PATCH,
                "/api/v1/patients/" + patientId + "/deceased",
                request);
    }
    
    protected ResultActions deceasedAsSecurityAdmin(
            UUID patientId,
            DeceasedPatientRequest request)
            throws Exception {

        return performAsSecurityAdmin(
                HttpMethod.PATCH,
                "/api/v1/patients/" + patientId + "/deceased",
                request);
    }
    
    private Patient reloadPatient() {

        return reload(
                repository,
                patient.getId());
    }
    
    private void assertDeceased(
            Patient patient) {

        assertThat(patient.getStatus())
                .isEqualTo(PatientStatus.DECEASED);

        assertThat(patient.getUpdatedAt())
                .isNotNull();

        assertThat(patient.getUpdatedBy())
                .isEqualTo("admin");

        assertThat(patient.getDeceasedDate())
                .isNotNull();

        assertThat(patient.getCauseOfDeath())
                .isNotBlank();
    }
    
    private void assertNotDeceased(
            Patient patient) {

        assertThat(patient.getStatus())
                .isEqualTo(PatientStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Should mark patient as deceased")
    void shouldMarkPatientAsDeceased()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        assertDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should return 404 when patient does not exist")
    void shouldReturnNotFoundWhenPatientDoesNotExist()
            throws Exception {

        deceased(
                UUID.randomUUID(),
                PatientTestDataFactory.validDeceasedRequest())

                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should reject blank cause of death")
    void shouldRejectBlankCauseOfDeath()
            throws Exception {

        deceased(
                patient.getId(),
                PatientTestDataFactory.blankCauseOfDeathRequest())

                .andExpect(status().isBadRequest());

        assertNotDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject null cause of death")
    void shouldRejectNullCauseOfDeath()
            throws Exception {

        deceased(
                patient.getId(),
                PatientTestDataFactory.nullCauseOfDeathRequest())

                .andExpect(status().isBadRequest());

        assertNotDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject future date of death")
    void shouldRejectFutureDate()
            throws Exception {

        deceased(
                patient.getId(),
                PatientTestDataFactory.futureDateOfDeathRequest())

                .andExpect(status().isBadRequest());

        assertNotDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject blank reason")
    void shouldRejectBlankReason()
            throws Exception {

        deceased(
                patient.getId(),
                PatientTestDataFactory.blankReasonRequest())

                .andExpect(status().isBadRequest());

        assertNotDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject already deceased patient")
    void shouldRejectAlreadyDeceasedPatient()
            throws Exception {

        Patient deceasedPatient =
                repository.save(
                        PatientTestDataFactory.deceasedPatient());

        deceased(
                deceasedPatient.getId(),
                PatientTestDataFactory.validDeceasedRequest())

                .andExpect(status().isConflict());

        assertThat(
                repository.findById(deceasedPatient.getId())
                        .orElseThrow()
                        .getStatus())
                .isEqualTo(PatientStatus.DECEASED);
    }
    
    @Test
    @DisplayName("Should reject archived patient")
    void shouldRejectArchivedPatient()
            throws Exception {

        Patient archived =
                repository.save(
                        PatientTestDataFactory.archivedPatient());
        
        deceased(
                archived.getId(),
                PatientTestDataFactory.validDeceasedRequest())

                .andExpect(status().isConflict());

        assertThat(
                repository.findById(archived.getId())
                        .orElseThrow()
                        .getStatus())
                .isEqualTo(PatientStatus.ARCHIVED);
    }
    
    @Test
    @DisplayName("Should reject second deceased request")
    void shouldRejectSecondRequest()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        deceased(patient.getId(), request)

                .andExpect(status().isConflict());

        assertDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should update audit information when patient is marked deceased")
    void shouldUpdateAuditInformation()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        Patient before =
                reloadPatient();

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        Patient deceased =
                reloadPatient();

        assertThat(deceased.getUpdatedAt())
                .isNotNull();

        assertThat(deceased.getUpdatedBy())
                .isEqualTo("admin");

        assertThat(deceased.getCreatedAt())
                .isNotNull();

        assertThat(deceased.getCreatedBy())
                .isEqualTo(before.getCreatedBy());

        assertThat(deceased.getUpdatedAt())
                .isNotNull();
    }
    
    @Test
    @DisplayName("Should reject deceased request without authentication")
    void shouldRejectWithoutAuthentication()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceasedWithoutAuthentication(
                patient.getId(),
                request)

                .andExpect(status().isUnauthorized());

        assertNotDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should reject deceased request without permission")
    void shouldRejectWithoutPermission()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceasedAsSecurityAdmin(
                patient.getId(),
                request)

                .andExpect(status().isForbidden());

        assertNotDeceased(
                reloadPatient());
    }
    
    @Test
    @DisplayName("Should persist deceased patient")
    void shouldPersistDeceasedPatient()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        Patient entity =
                repository.findById(patient.getId())
                        .orElseThrow();

        assertThat(entity.getStatus())
                .isEqualTo(PatientStatus.DECEASED);
    }
    
    @Test
    @DisplayName("Should persist death information")
    void shouldPersistDeathInformation()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        Patient entity =
                reloadPatient();

        assertThat(entity.getStatus())
                .isEqualTo(PatientStatus.DECEASED);

        assertThat(entity.getDeceasedDate())
                .isEqualTo(request.getDeceasedDate());

        assertThat(entity.getCauseOfDeath())
                .isEqualTo(request.getCauseOfDeath());

        assertThat(entity.getUpdatedBy())
                .isEqualTo("admin");

        assertThat(entity.getUpdatedAt())
                .isNotNull();
    }
    
    @Test
    @DisplayName("Should publish patient marked deceased event")
    void shouldPublishPatientMarkedDeceasedEvent()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        /*
         * Reset AFTER authentication if your BaseIntegrationTest
         * authenticates during performAuthenticatedRequest().
         */
        reset(publisher);

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        verify(publisher)
                .publish(any(PatientDeceasedEvent.class));
    }
    
    @Test
    @DisplayName("Should publish marked deceased event with correct values")
    void shouldPublishMarkedDeceasedEventWithCorrectValues()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        reset(publisher);

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        ArgumentCaptor<PatientDeceasedEvent> captor =
                ArgumentCaptor.forClass(
                		PatientDeceasedEvent.class);

        verify(publisher)
                .publish(captor.capture());

        PatientDeceasedEvent event =
                captor.getValue();

        assertThat(event.getEntityId())
                .isEqualTo(patient.getId().toString());

        assertThat(event.getPatientNumber())
                .isEqualTo(patient.getPatientNumber());

        assertThat(event.getUsername())
                .isEqualTo("admin");

        assertThat(event.getCauseOfDeath())
                .isEqualTo(request.getCauseOfDeath());
    }
    
    @Test
    @DisplayName("Should return deceased patient response")
    void shouldReturnDeceasedPatient()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        deceased(patient.getId(), request)

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id")
                        .value(patient.getId().toString()))

                .andExpect(jsonPath("$.patientNumber")
                        .value(patient.getPatientNumber()))

                .andExpect(jsonPath("$.status")
                        .value("DECEASED"))
                
                .andExpect(jsonPath("$.deceasedDate")
                        .value(request.getDeceasedDate().toString()))

                .andExpect(jsonPath("$.causeOfDeath")
                        .value(request.getCauseOfDeath()));;
        
        	
    }
    
    @Test
    @DisplayName("Should publish deceased event only once")
    void shouldPublishDeceasedEventOnlyOnce()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

        reset(publisher);

        deceased(patient.getId(), request)

                .andExpect(status().isOk());

        verify(publisher, times(1))
                .publish(any(PatientDeceasedEvent.class));
    }
    
    
    @Test
    @DisplayName("Should not publish event when validation fails")
    void shouldNotPublishEventWhenValidationFails()
            throws Exception {

        DeceasedPatientRequest request =
                PatientTestDataFactory.blankReasonRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/deceased", patient.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isBadRequest());

	    verifyNoInteractions(publisher);
    }
    
    @Test
    @DisplayName("Should not publish event when patient is already deceased")
    void shouldNotPublishEventWhenAlreadyDeceased()
            throws Exception {

        Patient deceasedPatient =
                repository.save(
                        PatientTestDataFactory.deceasedPatient());

        DeceasedPatientRequest request =
                PatientTestDataFactory.validDeceasedRequest();

	    String token = bearerToken();

	    reset(publisher);

	    mockMvc.perform(
	            patch("/api/v1/patients/{id}/deceased", deceasedPatient.getId())
	                    .header(HttpHeaders.AUTHORIZATION, token)
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isConflict());

	    verifyNoInteractions(publisher);
    }
    
    
}

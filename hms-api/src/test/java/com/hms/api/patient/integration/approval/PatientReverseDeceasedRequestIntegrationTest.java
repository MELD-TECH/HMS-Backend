package com.hms.api.patient.integration.approval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.hms.events.security.patient.PatientDeceasedReversalRequestedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.approval.dto.request.RequestReverseDeceasedRequest;
import com.hms.patient.approval.entity.PatientReverseDeceasedRequest;
import com.hms.patient.approval.enums.ApprovalStatus;
import com.hms.patient.approval.repository.PatientReverseDeceasedRequestRepository;
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
                "/db/testdata/007_mfa_admin.sql",
                
                "/db/testdata/V35__insert_additional_roles.sql",
                "/db/testdata/002_test_users.sql",
                "/db/testdata/003_user_roles.sql",
                "/db/testdata/005_password_history.sql",
                "/db/testdata/005_mfa.sql"
        },
        executionPhase =
                Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class PatientReverseDeceasedRequestIntegrationTest
        extends BaseIntegrationTest {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientReverseDeceasedRequestRepository
	        approvalRepository;

	@MockitoBean
	private SecurityEventPublisher publisher;

	private Patient patient;
	
	@BeforeEach
	void setup() {

	    patient =

	            patientRepository.save(

	                    PatientTestDataFactory

	                            .deceasedPatient());
	}
	
	protected ResultActions requestReverse(

	        UUID patientId,

	        RequestReverseDeceasedRequest request)

	        throws Exception {

	    return performAuthenticatedRequest(

	            HttpMethod.POST,

	            "/api/v1/patients/" +
	                    patientId +
	                    "/reverse-deceased/request",

	            request);
	}
	
	protected ResultActions requestWithoutAuthentication(

	        UUID patientId,

	        RequestReverseDeceasedRequest request)

	        throws Exception {

	    return mockMvc.perform(

	            post(

	                    "/api/v1/patients/{id}/reverse-deceased/request",

	                    patientId)

	                    .contentType(

	                            MediaType.APPLICATION_JSON)

	                    .content(

	                            objectMapper.writeValueAsString(
	                                    request)));
	}
	
	protected ResultActions requestAsSecurityAdmin(

	        UUID patientId,

	        RequestReverseDeceasedRequest request)

	        throws Exception {

	    return mockMvc.perform(

	            post(

	                    "/api/v1/patients/{id}/reverse-deceased/request",

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

	    return patientRepository

	            .findById(
	                    patient.getId())

	            .orElseThrow();
	}
	
	private PatientReverseDeceasedRequest reloadApprovalRequest() {

	    return approvalRepository

	            .findByPatientId(patient.getId())

	            .orElseThrow();
	}
	
	private void assertPendingRequest(

	        PatientReverseDeceasedRequest request) {

	    assertThat(request)

	            .isNotNull();

	    assertThat(request.getStatus())

	            .isEqualTo(
	                    ApprovalStatus.PENDING);

	    assertThat(request.getRequestedBy())

	            .isEqualTo("admin");

	    assertThat(request.getRequestedAt())

	            .isNotNull();
	}
	
	private void assertPatientStillDeceased(
	        Patient patient) {

	    assertThat(patient.getStatus())

	            .isEqualTo(
	                    PatientStatus.DECEASED);

	    assertThat(patient.getDeceased())

	            .isTrue();

	    assertThat(patient.getDeceasedDate())

	            .isNotNull();
	}
	
	
	@Test
	@DisplayName("Should create reverse deceased request")
	void shouldCreateReverseDeceasedRequest()
	        throws Exception {

	    RequestReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .validReverseDeceasedRequest();

	    requestReverse(
	            patient.getId(),
	            request)

	            .andExpect(status().isCreated());

	    PatientReverseDeceasedRequest approval =
	            reloadApprovalRequest();

	    assertPendingRequest(approval);

	    assertPatientStillDeceased(
	            reloadPatient());
	}
	
	@Test
	@DisplayName("Should return not found when patient does not exist")
	void shouldReturnNotFoundWhenPatientDoesNotExist()
	        throws Exception {

	    RequestReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .validReverseDeceasedRequest();

	    requestReverse(
	            UUID.randomUUID(),
	            request)

	            .andExpect(status().isNotFound());

	    assertThat(
	            approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Should reject active patient")
	void shouldRejectActivePatient()
	        throws Exception {

	    Patient active =
	            patientRepository.save(
	                    PatientTestDataFactory.activePatient());

	    RequestReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .validReverseDeceasedRequest();

	    requestReverse(
	            active.getId(),
	            request)

	            .andExpect(status().isBadRequest());

	    assertThat(
	            approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Should reject archived patient")
	void shouldRejectArchivedPatient()
	        throws Exception {

	    Patient archived =
	            patientRepository.save(
	                    PatientTestDataFactory.archivedPatient());

	    RequestReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .validReverseDeceasedRequest();

	    requestReverse(
	            archived.getId(),
	            request)

	            .andExpect(status().isBadRequest());

	    assertThat(
	            approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Should reject duplicate pending request")
	void shouldRejectDuplicatePendingRequest()
	        throws Exception {

	    approvalRepository.save(
	            PatientTestDataFactory
	                    .pendingReverseRequest(patient));

	    RequestReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .validReverseDeceasedRequest();

	    requestReverse(
	            patient.getId(),
	            request)

	            .andExpect(status().isConflict());

	    assertThat(
	            approvalRepository.count())
	            .isEqualTo(1);
	}
	
	@Test
	@DisplayName("Should reject blank reason")
	void shouldRejectBlankReason()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory
	                    .blankReverseDeceasedRequest())

	            .andExpect(status().isBadRequest());

	    assertThat(
	            approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Should reject null reason")
	void shouldRejectNullReason()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory
	                    .nullReverseDeceasedRequest())

	            .andExpect(status().isBadRequest());

	    assertThat(
	            approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Should reject reason exceeding maximum length")
	void shouldRejectLongReason()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory
	                    .longReverseDeceasedRequest())

	            .andExpect(status().isBadRequest());

	    assertThat(
	            approvalRepository.count())
	            .isZero();
	}
	
	
	@Test
	@DisplayName("Should reject short reason")
	void shouldRejectShortReason()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory
	                    .reverseDeceasedRequest("abc"))

	            .andExpect(status().isBadRequest());
	}
	

	@Test
	@DisplayName("Should populate request audit fields")
	void shouldPopulateRequestAuditFields()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isCreated());

	    PatientReverseDeceasedRequest request =
	            reloadApprovalRequest();

	    assertThat(request.getRequestedBy())
	            .isEqualTo("admin");

	    assertThat(request.getRequestedAt())
	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should populate entity audit fields")
	void shouldPopulateEntityAuditFields()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isCreated());

	    PatientReverseDeceasedRequest request =
	            reloadApprovalRequest();

	    assertThat(request.getCreatedBy())
	            .isEqualTo("admin");

	    assertThat(request.getCreatedAt())
	            .isNotNull();

	    assertThat(request.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(request.getUpdatedAt())
	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should not modify patient audit fields")
	void shouldNotModifyPatientAuditFields()
	        throws Exception {

	    Patient original =
	            reloadPatient();

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isCreated());

	    Patient updated =
	            reloadPatient();

	    assertThat(updated.getCreatedAt())
        .isEqualToIgnoringNanos(
                original.getCreatedAt());

	    assertThat(updated.getCreatedBy())
	            .isEqualTo(original.getCreatedBy());

	    assertThat(updated.getUpdatedAt())
	            .isEqualToIgnoringNanos(original.getUpdatedAt());

	    assertThat(updated.getUpdatedBy())
	            .isEqualTo(original.getUpdatedBy());
	}
	
	@Test
	@DisplayName("Should reject unauthenticated request")
	void shouldRejectUnauthenticatedRequest()
	        throws Exception {

	    requestWithoutAuthentication(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isUnauthorized());

	    assertThat(approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Receptionist should not request reverse deceased")
	void receptionistShouldNotRequestReverse()
	        throws Exception {

	    mockMvc.perform(

	            post("/api/v1/patients/{id}/reverse-deceased/request",
	                    patient.getId())

	                    .header(
	                            HttpHeaders.AUTHORIZATION,
	                            bearerToken("receptionist"))

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            asJson(
	                                    PatientTestDataFactory
	                                            .validReverseDeceasedRequest())))

	            .andExpect(status().isForbidden());

	    assertThat(approvalRepository.count())
	            .isZero();
	}
	
	@Test
	@DisplayName("Security Admin may create request")
	void securityAdminMayRequest()
	        throws Exception {

	    requestAsSecurityAdmin(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	    .andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Patient should remain unchanged after unauthorized request")
	void patientShouldRemainUnchangedAfterUnauthorized()
	        throws Exception {

	    requestWithoutAuthentication(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isUnauthorized());

	    Patient patient =
	            reloadPatient();

	    assertThat(patient.getStatus())
	            .isEqualTo(
	                    PatientStatus.DECEASED);

	    assertThat(patient.getDeceased())
	            .isTrue();
	}
	
	@Test
	@DisplayName("Should publish reverse deceased requested event")
	void shouldPublishReverseRequestEvent()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isCreated());

	    verify(publisher, times(1))

	            .publish(any(
	                    PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should publish correct event payload")
	void shouldPublishCorrectEvent()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isCreated());

	    ArgumentCaptor<PatientDeceasedReversalRequestedEvent> captor =

	            ArgumentCaptor.forClass(
	                    PatientDeceasedReversalRequestedEvent.class);

	    verify(publisher)

	            .publish(captor.capture());

	    PatientDeceasedReversalRequestedEvent event =
	            captor.getValue();

	    assertThat(event.getEntityId())
	            .isEqualTo(patient.getId().toString());

	    assertThat(event.getPatientNumber())
	            .isEqualTo(patient.getPatientNumber());

	    assertThat(event.getUsername())
	            .isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Should not publish event for invalid request")
	void shouldNotPublishEventForValidationFailure()
	        throws Exception {

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.blankReverseDeceasedRequest())

	            .andExpect(status().isBadRequest());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event for duplicate request")
	void shouldNotPublishEventForDuplicateRequest()
	        throws Exception {

	    approvalRepository.save(

	            PatientTestDataFactory
	                    .pendingReverseRequest(
	                            patient,
	                            "doctor"));

	    requestReverse(
	            patient.getId(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isConflict());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event when patient does not exist")
	void shouldNotPublishEventWhenPatientMissing()
	        throws Exception {

	    requestReverse(
	            UUID.randomUUID(),
	            PatientTestDataFactory.validReverseDeceasedRequest())

	            .andExpect(status().isNotFound());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	
	
}

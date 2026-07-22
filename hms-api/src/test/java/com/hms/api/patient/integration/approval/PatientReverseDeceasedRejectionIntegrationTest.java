package com.hms.api.patient.integration.approval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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
import com.hms.events.security.patient.PatientDeceasedReversalRejectedEvent;
import com.hms.events.security.patient.PatientDeceasedReversalRequestedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.approval.dto.request.RejectReverseDeceasedRequest;
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
public class PatientReverseDeceasedRejectionIntegrationTest  
extends BaseIntegrationTest{
	
	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientReverseDeceasedRequestRepository approvalRepository;

	@MockitoBean
	private SecurityEventPublisher publisher;

	private Patient patient;
	
	@BeforeEach
	void setup() {

	    patient = patientRepository.save(
	            PatientTestDataFactory.deceasedPatient());
	}
	
	private PatientReverseDeceasedRequest
	createPendingApprovalRequest() {

	    return approvalRepository.save(

	            PatientTestDataFactory
	                    .pendingReverseRequest(
	                            patient,
	                            "doctor"));
	}
	
	protected ResultActions rejectReverse(
	        UUID approvalId,
	        RejectReverseDeceasedRequest request)
	        throws Exception {

	    return performAuthenticatedRequest(

	            HttpMethod.POST,

	            "/api/v1/patient-approval/reverse-deceased/"
	                    + approvalId
	                    + "/reject",

	            request);
	}
	
	private PatientReverseDeceasedRequest
	reloadApprovalRequest() {

	    return approvalRepository

	    		.findByPatientId(patient.getId())

	            .orElseThrow();
	}
	
	private void assertRejected(
	        PatientReverseDeceasedRequest request) {

	    assertThat(request.getStatus())

	            .isEqualTo(
	                    ApprovalStatus.REJECTED);

	    assertThat(request.getRejectedBy())

	            .isEqualTo("admin");

	    assertThat(request.getRejectedAt())

	            .isNotNull();

	    assertThat(request.getRejectionReason())

	            .isEqualTo(
	                    "Death record has been verified and confirmed.");
	}
	
	private void assertPatientStillDeceased(
	        Patient patient) {

	    assertThat(patient.getStatus())

	            .isEqualTo(
	                    PatientStatus.DECEASED);

	    assertThat(patient.getDeceased())

	            .isTrue();
	}
	
	private Patient reloadPatient() {

	    return patientRepository

	            .findById(
	                    patient.getId())

	            .orElseThrow();
	}

	protected ResultActions rejectWithoutAuthentication(
	        UUID approvalId,
	        RejectReverseDeceasedRequest request)
	        throws Exception {

	    return mockMvc.perform(

	            post("/api/v1/patient-approval/reverse-deceased/{id}/reject",
	                    approvalId)

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(request)));
	}
	
	
	@Test
	@DisplayName("Should reject reverse deceased request")
	void shouldRejectReverseRequest()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    rejectReverse(

	            approval.getId(),

	            PatientTestDataFactory
	                    .rejectReverseRequest())

	            .andExpect(status().isNoContent());

	    assertRejected(
	            reloadApprovalRequest());

	    assertPatientStillDeceased(
	            reloadPatient());
	    
	    approval =
	            reloadApprovalRequest();

	    assertThat(approval.getStatus())
	            .isEqualTo(
	                    ApprovalStatus.REJECTED);

	    assertThat(approval.getRejectedBy())
	            .isEqualTo("admin");

	    assertThat(approval.getRejectedAt())
	            .isNotNull();

	    assertThat(approval.getRejectionReason())
	            .isEqualTo(
	                    "Death record has been verified and confirmed.");
	    
	    assertThat(approval.getRequestedBy())
        .isEqualTo("doctor");

	    assertThat(approval.getRequestedAt())
        .isNotNull();

	    assertThat(approval.getReason())
        .isEqualTo(
                "Death recorded in error.");
	    
	    Patient patient =
	            reloadPatient();

	    assertThat(patient.getStatus())
	            .isEqualTo(
	                    PatientStatus.DECEASED);

	    assertThat(patient.getDeceased())
	            .isTrue();
	}
	
	@Test
	@DisplayName("Should return not found when approval request does not exist")
	void shouldReturnNotFoundWhenRequestDoesNotExist()
	        throws Exception {

	    rejectReverse(

	            UUID.randomUUID(),

	            PatientTestDataFactory
	                    .rejectReverseRequest())

	            .andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should reject already approved request")
	void shouldRejectAlreadyApproved()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =

	            approvalRepository.save(

	                    PatientTestDataFactory
	                            .approvedReverseRequest(patient));

	    rejectReverse(

	            approval.getId(),

	            PatientTestDataFactory
	                    .rejectReverseRequest())

	            .andExpect(status().isConflict());
	}
	
	@Test
	@DisplayName("Should reject already rejected request")
	void shouldRejectAlreadyRejected()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =

	            approvalRepository.save(

	                    PatientTestDataFactory
	                            .rejectedReverseRequest(patient));

	    rejectReverse(

	            approval.getId(),

	            PatientTestDataFactory
	                    .rejectReverseRequest())

	            .andExpect(status().isConflict());
	}
	
	@Test
	@DisplayName("Maker should not reject own request")
	void makerCannotRejectOwnRequest()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    mockMvc.perform(

	            post("/api/v1/patient-approval/reverse-deceased/{id}/reject",
	                    approval.getId())

	                    .header(
	                            HttpHeaders.AUTHORIZATION,

	                            bearerToken("doctor"))

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(

	                            objectMapper.writeValueAsString(

	                                    PatientTestDataFactory
	                                            .rejectReverseRequest())))

	            .andExpect(status().isForbidden());

	    assertThat(
	            reloadApprovalRequest().getStatus())

	            .isEqualTo(
	                    ApprovalStatus.PENDING);
	}	

	@Test
	@DisplayName("Should populate rejection audit fields")
	void shouldPopulateRejectionAuditFields()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isNoContent());

	    PatientReverseDeceasedRequest saved =
	            reloadApprovalRequest();

	    assertThat(saved.getStatus())
	            .isEqualTo(ApprovalStatus.REJECTED);

	    assertThat(saved.getRejectedBy())
	            .isEqualTo("admin");

	    assertThat(saved.getRejectedAt())
	            .isNotNull();

	    assertThat(saved.getRejectionReason())
	            .isEqualTo("Death record has been verified and confirmed.");
	}
	
	@Test
	@DisplayName("Should preserve original request information")
	void shouldPreserveOriginalRequest()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    LocalDateTime requestedAt =
	            request.getRequestedAt();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isNoContent());

	    PatientReverseDeceasedRequest saved =
	            reloadApprovalRequest();

	    assertThat(saved.getRequestedBy())
	            .isEqualTo("doctor");

	    assertThat(saved.getRequestedAt())
	            .isEqualToIgnoringNanos(requestedAt);

	    assertThat(saved.getReason())
	            .isEqualTo("Death recorded in error.");
	}
	
	@Test
	@DisplayName("Should update entity audit information")
	void shouldUpdateEntityAudit()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isNoContent());

	    PatientReverseDeceasedRequest saved =
	            reloadApprovalRequest();

	    assertThat(saved.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(saved.getUpdatedAt())
	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should not modify patient audit information")
	void shouldNotModifyPatientAudit()
	        throws Exception {

	    Patient original =
	            reloadPatient();

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isNoContent());

	    Patient patient =
	            reloadPatient();

	    assertThat(patient.getCreatedBy())
	            .isEqualTo(original.getCreatedBy());

	    assertThat(patient.getCreatedAt())
	            .isEqualToIgnoringNanos(original.getCreatedAt());

	    assertThat(patient.getUpdatedBy())
	            .isEqualTo(original.getUpdatedBy());

	    assertThat(patient.getUpdatedAt())
	            .isEqualToIgnoringNanos(original.getUpdatedAt());
	}
	
	@Test
	@DisplayName("Should reject unauthenticated rejection")
	void shouldRejectUnauthenticatedRejection()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectWithoutAuthentication(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isUnauthorized());

	    assertThat(reloadApprovalRequest().getStatus())
	            .isEqualTo(ApprovalStatus.PENDING);
	}
	
	@Test
	@DisplayName("Receptionist should not reject request")
	void receptionistCannotReject()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    mockMvc.perform(

	            post("/api/v1/patient-approval/reverse-deceased/{id}/reject",
	                    request.getId())

	                    .header(HttpHeaders.AUTHORIZATION,
	                            bearerToken("receptionist"))

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(asJson(
	                            PatientTestDataFactory.rejectReverseRequest())))

	            .andExpect(status().isForbidden());

	    assertThat(reloadApprovalRequest().getStatus())
	            .isEqualTo(ApprovalStatus.PENDING);
	}
	
	@Test
	@DisplayName("Patient should remain deceased after failed rejection")
	void patientShouldRemainDeceasedAfterFailedRejection()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectWithoutAuthentication(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isUnauthorized());

	    Patient patient =
	            reloadPatient();

	    assertThat(patient.getStatus())
	            .isEqualTo(PatientStatus.DECEASED);

	    assertThat(patient.getDeceased())
	            .isTrue();
	}
	
	@Test
	@DisplayName("Should publish rejection event")
	void shouldPublishRejectionEvent()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isNoContent());

	    verify(publisher)
	            .publish(any(
	                    PatientDeceasedReversalRejectedEvent.class));
	}
	
	@Test
	@DisplayName("Should publish rejection event once")
	void shouldPublishEventOnce()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest());

	    verify(publisher, times(1))
        .publish(any(
                PatientDeceasedReversalRejectedEvent.class));
	}
	
	@Test
	@DisplayName("Should publish correct rejection event")
	void shouldPublishCorrectRejectionEvent()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest());

	    ArgumentCaptor<PatientDeceasedReversalRejectedEvent> captor =
	            ArgumentCaptor.forClass(
	                    PatientDeceasedReversalRejectedEvent.class);

	    verify(publisher)
	            .publish(captor.capture());

	    PatientDeceasedReversalRejectedEvent event =
	            captor.getValue();

	    assertThat(event.getEntityId())
	            .isEqualTo(patient.getId().toString());

	    assertThat(event.getPatientNumber())
	            .isEqualTo(patient.getPatientNumber());

	    assertThat(event.getUsername())
	            .isEqualTo("admin");
	}
	
	@Test
	@DisplayName("Should not publish event for validation failure")
	void shouldNotPublishEventForValidationFailure()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            createPendingApprovalRequest();

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.blankRejectRequest())

	            .andExpect(status().isBadRequest());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event when request already rejected")
	void shouldNotPublishEventWhenAlreadyRejected()
	        throws Exception {

	    PatientReverseDeceasedRequest request =
	            approvalRepository.save(
	                    PatientTestDataFactory
	                            .rejectedReverseRequest(patient));

	    rejectReverse(
	            request.getId(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isConflict());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event when request is missing")
	void shouldNotPublishEventWhenRequestMissing()
	        throws Exception {

	    rejectReverse(
	            UUID.randomUUID(),
	            PatientTestDataFactory.rejectReverseRequest())

	            .andExpect(status().isNotFound());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	
}

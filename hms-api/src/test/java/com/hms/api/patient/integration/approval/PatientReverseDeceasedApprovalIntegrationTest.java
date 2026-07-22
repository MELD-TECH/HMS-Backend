package com.hms.api.patient.integration.approval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
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
import com.hms.events.security.patient.PatientDeceasedReversalApprovedEvent;
import com.hms.events.security.patient.PatientDeceasedReversalRequestedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.approval.dto.request.ApproveReverseDeceasedRequest;
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
public class PatientReverseDeceasedApprovalIntegrationTest 
	extends BaseIntegrationTest{

	
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
	
	protected ResultActions approveWithoutAuthentication(
	        UUID approvalId,
	        ApproveReverseDeceasedRequest request)
	        throws Exception {

	    return mockMvc.perform(

	            post(
	                    "/api/v1/patient-approval/reverse-deceased/{id}/approve",
	                    approvalId)

	                    .contentType(MediaType.APPLICATION_JSON)

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
	
	private Patient reloadPatient(UUID patientId) {

	    return patientRepository

	            .findById(patientId)

	            .orElseThrow();
	}
	
	private PatientReverseDeceasedRequest
	reloadApprovalRequest() {

	    return approvalRepository

	    		.findByPatientId(patient.getId())

	            .orElseThrow();
	}
	
	protected ResultActions approveReverse(
	        UUID approvalId,
	        ApproveReverseDeceasedRequest request)
	        throws Exception {

	    return performAuthenticatedRequest(

	            HttpMethod.POST,

	            "/api/v1/patient-approval/reverse-deceased/"
	                    + approvalId
	                    + "/approve",

	            request);
	}
	
	private void assertApproved(
	        PatientReverseDeceasedRequest request) {

	    assertThat(request.getStatus())
	            .isEqualTo(
	                    ApprovalStatus.APPROVED);

	    assertThat(request.getApprovedBy())
	            .isEqualTo("admin");

	    assertThat(request.getApprovedAt())
	            .isNotNull();

	    assertThat(request.getApprovalComment())
	            .isEqualTo(
	                    "Verified");
	}
	
	private void assertPatientRestored(
	        Patient patient) {

	    assertThat(patient.getStatus())
	            .isEqualTo(
	                    PatientStatus.ACTIVE);

	    assertThat(patient.getDeceased())
	            .isFalse();
	}
	
	private PatientReverseDeceasedRequest
	createPendingApprovalRequest() {

	    return approvalRepository.save(

	            PatientTestDataFactory
	                    .pendingReverseRequest(patient));
	}
	
	@Test
	@DisplayName("Should approve reverse deceased request")
	void shouldApproveReverseRequest()
	        throws Exception {

		PatientReverseDeceasedRequest approval =
		        createPendingApprovalRequest();

	    ApproveReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .approveReverseRequest();

	    approveReverse(
	            approval.getId(),
	            request)

	            .andExpect(status().isOk());

	    Patient updated =
	            reloadPatient(
	                    approval.getPatient().getId());

	    PatientReverseDeceasedRequest saved =
	            reloadApprovalRequest();

	    assertPatientRestored(updated);

	    assertApproved(saved);
	    
	    assertThat(approval.getStatus())
	            .isEqualTo(
	                    ApprovalStatus.APPROVED);

	    assertThat(approval.getApprovedBy())
	            .isEqualTo("admin");

	    assertThat(approval.getApprovedAt())
	            .isNotNull();

	    assertThat(approval.getApprovalComment())
	            .isEqualTo("Verified");
	    
	    assertThat(approval.getRequestedBy())
        .isEqualTo("doctor");

	    assertThat(approval.getRequestedAt())
        	.isNotNull();

	    assertThat(approval.getReason())
        	.isEqualTo(
                "Death recorded in error.");
	}
	
	@Test
	@DisplayName("Should return not found when approval request does not exist")
	void shouldReturnNotFoundWhenApprovalMissing()
	        throws Exception {

	    ApproveReverseDeceasedRequest request =
	            PatientTestDataFactory
	                    .approveReverseRequest();

	    approveReverse(
	            UUID.randomUUID(),
	            request)

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

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory
	                    .approveReverseRequest())

	            .andExpect(status().isConflict());
	}
	
	@Test
	@DisplayName("Should reject rejected request")
	void shouldRejectRejectedRequest()
	        throws Exception {

		PatientReverseDeceasedRequest approval =
		        approvalRepository.save(
		                PatientTestDataFactory
		                        .rejectedReverseRequest(patient));

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory
	                    .approveReverseRequest())

	            .andExpect(status().isConflict());
	}
	
	@Test
	@DisplayName("Maker should not approve own request")
	void makerCannotApproveOwnRequest()
	        throws Exception {

		PatientReverseDeceasedRequest approval =
		        createPendingApprovalRequest();

	    String makerToken =
	    		bearerToken("doctor");

	    mockMvc.perform(

	            post("/api/v1/patient-approval/reverse-deceased/{id}/approve",
	                    approval.getId())

	                    .header(HttpHeaders.AUTHORIZATION,
	                            makerToken)

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            objectMapper.writeValueAsString(
	                                    PatientTestDataFactory
	                                            .approveReverseRequest())))

	    .andExpect(status().isForbidden());

	    assertThat(
	            reloadApprovalRequest().getStatus())

	            .isEqualTo(
	                    ApprovalStatus.PENDING);
	}
	
	@Test
	@DisplayName("Should reject blank approval comment")
	void shouldRejectBlankApprovalComment()
	        throws Exception {

		PatientReverseDeceasedRequest approval =
		        createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory
	                    .blankApprovalRequest())

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should reject long approval comment")
	void shouldRejectLongApprovalComment()
	        throws Exception {

		PatientReverseDeceasedRequest approval =
		        createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory
	                    .longApprovalRequest())

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should populate approval audit fields")
	void shouldPopulateApprovalAuditFields()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isOk());

	    PatientReverseDeceasedRequest saved =
	            reloadApprovalRequest();

	    assertThat(saved.getApprovedBy())
	            .isEqualTo("admin");

	    assertThat(saved.getApprovedAt())
	            .isNotNull();

	    assertThat(saved.getApprovalComment())
	            .isEqualTo("Verified");
	}
	
	@Test
	@DisplayName("Should preserve original request details")
	void shouldPreserveOriginalRequest()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    LocalDateTime requestedAt =
	            approval.getRequestedAt();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isOk());

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

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isOk());

	    PatientReverseDeceasedRequest saved =
	            reloadApprovalRequest();

	    assertThat(saved.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(saved.getUpdatedAt())
	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should update patient audit information")
	void shouldUpdatePatientAudit()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isOk());

	    Patient updated =
	            reloadPatient(
	                    approval.getPatient().getId());

	    assertThat(updated.getUpdatedBy())
	            .isEqualTo("admin");

	    assertThat(updated.getUpdatedAt())
	            .isNotNull();
	}
	
	@Test
	@DisplayName("Should reject unauthenticated approval")
	void shouldRejectUnauthenticatedApproval()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    mockMvc.perform(

	            post("/api/v1/patients/reverse-deceased/{id}/approve",
	                    approval.getId())

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            asJson(
	                                    PatientTestDataFactory
	                                            .approveReverseRequest())))

	            .andExpect(status().isUnauthorized());

	    assertThat(
	            reloadApprovalRequest().getStatus())

	            .isEqualTo(
	                    ApprovalStatus.PENDING);
	}
	
	@Test
	@DisplayName("Receptionist should not approve request")
	void receptionistCannotApprove()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    mockMvc.perform(

	            post("/api/v1/patient-approval/reverse-deceased/{id}/approve",
	                    approval.getId())

	                    .header(
	                            HttpHeaders.AUTHORIZATION,
	                            bearerToken("receptionist"))

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(
	                            asJson(
	                                    PatientTestDataFactory
	                                            .approveReverseRequest())))

	            .andExpect(status().isForbidden());

	    assertThat(
	            reloadApprovalRequest().getStatus())

	            .isEqualTo(
	                    ApprovalStatus.PENDING);
	}
	
	@Test
	@DisplayName("Patient should remain deceased after failed approval")
	void patientShouldRemainDeceasedAfterFailedApproval()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveWithoutAuthentication(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isUnauthorized());

	    Patient updated =
	            reloadPatient(
	                    approval.getPatient().getId());

	    assertThat(patient.getStatus())
	            .isEqualTo(PatientStatus.DECEASED);

	    assertThat(patient.getDeceased())
	            .isTrue();
	}
	
	@Test
	@DisplayName("Should publish approval event")
	void shouldPublishApprovalEvent()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isOk());

	    verify(publisher)

	            .publish(any(
	                    PatientDeceasedReversalApprovedEvent.class));
	}
	
	@Test
	@DisplayName("Should publish approval event once")
	void shouldPublishEventOnce()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest());

	    verify(publisher, times(1))
        .publish(any(
                PatientDeceasedReversalApprovedEvent.class));
	}
	
	@Test
	@DisplayName("Should publish correct approval event")
	void shouldPublishCorrectApprovalEvent()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest());

	    ArgumentCaptor<
	            PatientDeceasedReversalApprovedEvent> captor =

	            ArgumentCaptor.forClass(
	                    PatientDeceasedReversalApprovedEvent.class);

	    verify(publisher)

	            .publish(captor.capture());

	    PatientDeceasedReversalApprovedEvent event =
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

	    PatientReverseDeceasedRequest approval =
	            createPendingApprovalRequest();

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.blankApprovalRequest())

	            .andExpect(status().isBadRequest());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event for already approved request")
	void shouldNotPublishEventWhenAlreadyApproved()
	        throws Exception {

	    PatientReverseDeceasedRequest approval =
	            approvalRepository.save(
	                    PatientTestDataFactory
	                            .approvedReverseRequest(patient));

	    approveReverse(
	            approval.getId(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isConflict());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	@Test
	@DisplayName("Should not publish event when request is missing")
	void shouldNotPublishEventWhenRequestMissing()
	        throws Exception {

	    approveReverse(
	            UUID.randomUUID(),
	            PatientTestDataFactory.approveReverseRequest())

	            .andExpect(status().isNotFound());

	    verify(publisher, never())

	    .publish(any(PatientDeceasedReversalRequestedEvent.class));
	}
	
	
}

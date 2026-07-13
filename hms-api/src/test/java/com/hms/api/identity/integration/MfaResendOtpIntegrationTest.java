package com.hms.api.identity.integration;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.audit.security.entity.AuditLog;
import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.identity.authentication.entity.PendingAuthentication;
import com.hms.identity.authentication.enums.PendingAuthenticationStatus;
import com.hms.identity.authentication.repository.PendingAuthenticationRepository;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.enums.OtpStatus;
import com.hms.notification.mfa.repository.OtpRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/003_roles.sql",
        "/db/testdata/004_role_permissions.sql",
        "/db/testdata/005_password_history.sql",
        "/db/testdata/007_mfa_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class MfaResendOtpIntegrationTest  extends BaseIntegrationTest{

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PendingAuthenticationRepository pendingRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private AuditLogRepository auditRepository;
	
	private JsonNode loginAndReturnChallenge()
	        throws Exception {

	    String response =

	            mockMvc.perform(

	                    post("/auth/login")

	                            .contentType(MediaType.APPLICATION_JSON)

	                            .content("""
	                            {
	                                "username":"admin",
	                                "password":"password"
	                            }
	                            """))

	                    .andExpect(status().isOk())

	                    .andReturn()

	                    .getResponse()

	                    .getContentAsString();

	    return objectMapper.readTree(response);
	}
	
	private OtpCode latestOtp() {

	    User user =

	            userRepository

	                    .findByUsername("admin")

	                    .orElseThrow();

	    return otpRepository

	            .findTopByUserIdAndTypeAndStatusOrderByCreatedAtDesc(

	                    user.getId(),

	                    MfaType.EMAIL,

	                    OtpStatus.ACTIVE)

	            .orElseThrow();
	}
	
	private ResultActions resendOtp(String challengeToken)
	        throws Exception {

	    return mockMvc.perform(

	            post("/auth/mfa/resend")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "challengeToken":"%s"
	                    }
	                    """.formatted(challengeToken)));
	}
	
	@Test
	void shouldResendOtp()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();

	    clearCooldown(
	            login.get("challengeToken").asText());
	    
	    mockMvc.perform(

	            post("/auth/mfa/resend")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s"
	                    }
	                    """.formatted(
	                            login.get("challengeToken").asText())))

	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.otpId").exists())
	            .andExpect(jsonPath("$.expiresAt").exists());
	}
	
	@Test
	void shouldCreateNewOtp()
	        throws Exception {

		JsonNode login = loginAndReturnChallenge();
		
	    clearCooldown(
	            login.get("challengeToken").asText());

	    OtpCode first = latestOtp();

	    resendOtp(login.get("challengeToken").asText())
        .andExpect(status().isOk());
	    
	    OtpCode second = latestOtp();

	    assertNotEquals(
	            first.getId(),
	            second.getId());

	    assertNotEquals(
	            first.getCode(),
	            second.getCode());
	}
	
	@Test
	void shouldExpirePreviousOtp()
	        throws Exception {

		JsonNode login = loginAndReturnChallenge();
		
	    clearCooldown(
	            login.get("challengeToken").asText());

	    OtpCode first = latestOtp();

	    resendOtp(login.get("challengeToken").asText())
        .andExpect(status().isOk());

	    OtpCode expired =

	            otpRepository

	                    .findById(first.getId())

	                    .orElseThrow();

	    assertEquals(
	            OtpStatus.EXPIRED,
	            expired.getStatus());
	}
	
	@Test
	void shouldReturnOnlyOneActiveOtp()
	        throws Exception {

		JsonNode login = loginAndReturnChallenge();
		
	    clearCooldown(
	            login.get("challengeToken").asText());

	    resendOtp(login.get("challengeToken").asText())
        .andExpect(status().isOk());

	    User user =

	            userRepository

	                    .findByUsername("admin")

	                    .orElseThrow();

	    long active =

	            otpRepository

	                    .findByUserIdAndStatus(

	                            user.getId(),

	                            OtpStatus.ACTIVE)

	                    .size();

	    assertEquals(
	            1,
	            active);
	}
	
	@Test
	void shouldRejectExpiredChallenge()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();
	    
	    clearCooldown(
	            login.get("challengeToken").asText());

	    PendingAuthentication pending =

	            pendingRepository

	                    .findByChallengeToken(

	                            login.get("challengeToken").asText())

	                    .orElseThrow();

	    pending.setExpiresAt(
	            LocalDateTime.now().minusMinutes(1));

	    pendingRepository.save(pending);

	    resendOtp(login.get("challengeToken").asText())
	    
	    .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectCompletedChallenge()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();
	    
	    clearCooldown(
	            login.get("challengeToken").asText());

	    PendingAuthentication pending =

	            pendingRepository

	                    .findByChallengeToken(

	                            login.get("challengeToken").asText())

	                    .orElseThrow();

	    pending.setStatus(
	            PendingAuthenticationStatus.COMPLETED);

	    pendingRepository.save(pending);

	    resendOtp(login.get("challengeToken").asText())
	    
	    .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldWriteOtpResentAudit()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();
	    
	    clearCooldown(
	            login.get("challengeToken").asText());

	    resendOtp(login.get("challengeToken").asText())
        .andExpect(status().isOk());
	    
	    AuditLog audit =

	            auditRepository

	                    .findAll()

	                    .stream()

	                    .filter(a ->

	                            "OTP_RESENT"

	                                    .equals(a.getAction()))

	                    .findFirst()

	                    .orElseThrow();

	    assertEquals(
	            "admin",
	            audit.getUsername());
	}
	
	@Test
	void shouldWriteOtpExpiredByResendAudit()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();
	    
	    clearCooldown(
	            login.get("challengeToken").asText());

	    resendOtp(login.get("challengeToken").asText())
        .andExpect(status().isOk());

	    AuditLog audit =

	            auditRepository

	                    .findAll()

	                    .stream()

	                    .filter(a ->

	                            "OTP_EXPIRED_BY_RESEND"

	                                    .equals(a.getAction()))

	                    .findFirst()

	                    .orElseThrow();

	    assertEquals(
	            "OTP_EXPIRED_BY_RESEND",
	            audit.getAction());
	}
	
	@Test
	void shouldResetOtpExpiry()
	        throws Exception {

		JsonNode login = loginAndReturnChallenge();
		
	    clearCooldown(
	            login.get("challengeToken").asText());

	    OtpCode first = latestOtp();

	    LocalDateTime firstExpiry = first.getExpiresAt();

	    Thread.sleep(1000);

	    resendOtp(login.get("challengeToken").asText())
        .andExpect(status().isOk());

	    OtpCode second = latestOtp();

	    assertTrue(
	            second.getExpiresAt()
	                    .isAfter(firstExpiry));
	}
	
	@Test
	void shouldRejectResendLimitExceeded()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();

	    for (int i = 0; i < 5; i++) {
		    
		    clearCooldown(
		            login.get("challengeToken").asText());

	    	resendOtp(login.get("challengeToken").asText())
	        .andExpect(status().isOk());
	    }

	    resendOtp(login.get("challengeToken").asText())
	    .andExpect(status().isTooManyRequests()); 

	}
	
	@Test
	void shouldRejectCooldownViolation()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();

//	    resendOtp(login.get("challengeToken").asText())
 //       .andExpect(status().isOk());

	    resendOtp(login.get("challengeToken").asText())
	    .andExpect(status().isTooManyRequests());

	}
	
    private void clearCooldown(String challengeToken) {

        PendingAuthentication pending =

        		pendingRepository

                        .findByChallengeToken(challengeToken)

                        .orElseThrow();

        pending.setLastOtpSentAt(

                LocalDateTime.now()

                        .minusSeconds(31));

        pendingRepository.save(pending);
    }

}

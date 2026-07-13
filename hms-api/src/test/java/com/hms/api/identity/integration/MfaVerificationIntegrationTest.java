package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

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
import com.hms.identity.session.repository.RefreshTokenRepository;
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
public class MfaVerificationIntegrationTest  extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PendingAuthenticationRepository pendingRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private AuditLogRepository auditRepository;
	
	private JsonNode loginAndReturnChallenge()
	        throws Exception {

	    String body =

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

	    return objectMapper.readTree(body);
	}
	
	private OtpCode latestEmailOtp(UUID userId) {

	    return otpRepository

	            .findTopByUserIdAndTypeAndStatusOrderByCreatedAtDesc(

	                    userId,

	                    MfaType.EMAIL,

	                    OtpStatus.ACTIVE)

	            .orElseThrow();
	}
	
	@Test
	void shouldCompleteMfaLogin()
	        throws Exception {

	    JsonNode login = loginAndReturnChallenge();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            otp.getCode())))

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.accessToken").exists())

	            .andExpect(jsonPath("$.refreshToken").exists())

	            .andExpect(jsonPath("$.mfaRequired").value(false));
	}
	
	@Test
	void shouldRejectInvalidOtp()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"999999"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText())))

	    .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectExpiredOtp()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    otp.setExpiresAt(

	            LocalDateTime.now().minusMinutes(1));

	    otpRepository.save(otp);

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            otp.getCode())))

	    .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectUsedOtp()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    String request = """
	    {
	      "challengeToken":"%s",
	      "otp":"%s"
	    }
	    """.formatted(

	            login.get("challengeToken").asText(),

	            otp.getCode());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(request))

	            .andExpect(status().isOk());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content(request))

	    .andExpect(status().isNotFound());
	}
	
	@Test
	void shouldMarkPendingAuthenticationCompleted()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            otp.getCode())))

	            .andExpect(status().isOk());

	    PendingAuthentication pending =

	            pendingRepository

	                    .findByChallengeToken(

	                            login.get("challengeToken").asText())

	                    .orElseThrow();

	    assertEquals(

	            PendingAuthenticationStatus.COMPLETED,

	            pending.getStatus());

	    assertNotNull(

	            pending.getCompletedAt());
	}
	
	@Test
	void shouldCreateRefreshToken()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            otp.getCode())))

	            .andExpect(status().isOk());

	    assertFalse(

	            refreshTokenRepository

	                    .findByUserId(user.getId())

	                    .isEmpty());
	}
	
	@Test
	void shouldWriteLoginSuccessAudit()
	        throws Exception {

	    completeLogin();

	    AuditLog audit =

	            auditRepository

	                    .findAll()

	                    .stream()

	                    .filter(a ->

	                            "LOGIN_SUCCESS"

	                                    .equals(a.getAction()))

	                    .findFirst()

	                    .orElseThrow();

	    assertEquals(

	            "admin",

	            audit.getUsername());
	}
	
	@Test
	void shouldWriteMfaVerifiedAudit()
	        throws Exception {

	    completeLogin();

	    AuditLog audit =

	            auditRepository

	                    .findAll()

	                    .stream()

	                    .filter(a ->

	                            "MFA_VERIFIED"

	                                    .equals(a.getAction()))

	                    .findFirst()

	                    .orElseThrow();

	    assertEquals(

	            "MFA_VERIFIED",

	            audit.getAction());
	}
	
	@Test
	void shouldRejectCompletedChallenge()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    completeLogin();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode usedOtp =

	    	    otpRepository

	    	        .findTopByUserIdOrderByCreatedAtDesc(user.getId())

	    	        .orElseThrow();

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            usedOtp.getCode())))

	    .andExpect(status().isNotFound());
	}
	
	@Test
	void shouldRejectExpiredChallenge()
	        throws Exception {

	    JsonNode login =
	            loginAndReturnChallenge();

	    PendingAuthentication pending =

	            pendingRepository

	                    .findByChallengeToken(

	                            login.get("challengeToken").asText())

	                    .orElseThrow();

	    pending.setExpiresAt(

	            LocalDateTime.now().minusMinutes(1));

	    pendingRepository.save(pending);

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            otp.getCode())))

	    .andExpect(status().isBadRequest());
	}
	
	private void completeLogin() throws Exception {

	    JsonNode login = loginAndReturnChallenge();

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	    		latestEmailOtp(user.getId());

	    mockMvc.perform(

	    		post("/auth/mfa/complete")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "challengeToken":"%s",
	                      "otp":"%s"
	                    }
	                    """.formatted(

	                            login.get("challengeToken").asText(),

	                            otp.getCode())))

	            .andExpect(status().isOk());
	}
	
	
}

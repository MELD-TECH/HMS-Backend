package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.audit.entity.AuditLog;
import com.hms.identity.audit.repository.AuditLogRepository;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.session.repository.RefreshTokenRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/003_roles.sql",
        "/db/testdata/004_role_permissions.sql",
        "/db/testdata/005_password_history.sql",
        "/db/testdata/006_security_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class PasswordExpiryIntegrationTest
        extends BaseIntegrationTest {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuditLogRepository auditRepository;
	
	@Autowired
	private RefreshTokenRepository refreshRepository;
	
	private void expirePassword() {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    user.setPasswordExpiresAt(
	            LocalDateTime.now()
	                    .minusDays(1));

	    userRepository.save(user);

	}
	
	@Test
	void shouldLoginWhenPasswordNotExpired()
	        throws Exception {

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isOk());

	}
	
	@Test
	void shouldRejectExpiredPassword()
	        throws Exception {

	    expirePassword();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden());

	}
	
	@Test
	void shouldReturnPasswordExpiredCode()
	        throws Exception {

	    expirePassword();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden())

	            .andExpect(jsonPath("$.code")
	                    .value("PASSWORD_EXPIRED"));

	}
	
	@Test
	void shouldReturnPasswordExpiredMessage()
	        throws Exception {

	    expirePassword();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden())

	            .andExpect(jsonPath("$.message")
	                    .value("Your password has expired. Please reset your password."));

	}
	
	@Test
	void shouldAuditPasswordExpired()
	        throws Exception {

	    expirePassword();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden());

	    AuditLog audit =
	            findAudit("PASSWORD_EXPIRED");

	    assertEquals(
	            "PASSWORD_EXPIRED",
	            audit.getAction());

	}
	
	@Test
	void shouldAuditCorrectUsername()
	        throws Exception {

	    expirePassword();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """));

	    AuditLog audit =
	            findAudit("PASSWORD_EXPIRED");

	    assertEquals(
	            "admin",
	            audit.getUsername());

	}
	
	@Test
	void shouldAuditEntityName()
	        throws Exception {

	    expirePassword();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """));

	    AuditLog audit =
	            findAudit("PASSWORD_EXPIRED");

	    assertEquals(
	            "USER",
	            audit.getEntityName());

	}
	
	@Test
	void shouldAuditCorrectEntityId()
	        throws Exception {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    user.setPasswordExpiresAt(
	            LocalDateTime.now().minusDays(1));

	    userRepository.save(user);

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """));

	    AuditLog audit =
	            findAudit("PASSWORD_EXPIRED", user.getId());

	    assertEquals(
	            user.getId().toString(),
	            audit.getEntityId());

	}
	
	@Test
	void shouldNotResetFailedAttemptsWhenPasswordExpired()
	        throws Exception {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    user.setFailedLoginAttempts(3);

	    user.setPasswordExpiresAt(
	            LocalDateTime.now()
	                    .minusDays(1));

	    userRepository.save(user);

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden());

	    User updated =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    assertEquals(
	            Integer.valueOf(3),
	            updated.getFailedLoginAttempts());

	}
	
	@Test
	void shouldNotIssueAccessTokenWhenPasswordExpired()
	        throws Exception {

	    expirePassword();

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

	                    .andExpect(status().isForbidden())

	                    .andReturn()

	                    .getResponse()

	                    .getContentAsString();

	    JsonNode json =
	            objectMapper.readTree(response);

	    assertFalse(
	            json.has("accessToken"));

	}
	

	@Test
	void shouldNotCreateRefreshTokenWhenPasswordExpired()
	        throws Exception {

	    expirePassword();

	    long before =
	            refreshRepository.count();

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden());

	    long after =
	            refreshRepository.count();

	    assertEquals(
	            before,
	            after);

	}
	
	@Test
	void shouldNotIncrementFailedAttemptsForExpiredPassword()
	        throws Exception {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    user.setPasswordExpiresAt(
	            LocalDateTime.now().minusDays(1));

	    user.setFailedLoginAttempts(0);

	    userRepository.save(user);

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                      "username":"admin",
	                      "password":"password"
	                    }
	                    """))

	            .andExpect(status().isForbidden());

	    User updated =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    assertEquals(
	            Integer.valueOf(0),
	            updated.getFailedLoginAttempts());

	    assertFalse(
	            updated.getAccountLocked());

	}
	
	private AuditLog findAudit(
	        String action,
	        UUID entityId) {

	    return auditRepository

	            .findTopByActionAndEntityIdOrderByCreatedAtDesc(
	                    action,
	                    entityId.toString())

	            .orElseThrow();
	}
	
	private AuditLog findAudit(
	        String action) {

	    return auditRepository
	            .findTopByActionOrderByCreatedAtDesc(action)
	            .orElseThrow();
	}
}
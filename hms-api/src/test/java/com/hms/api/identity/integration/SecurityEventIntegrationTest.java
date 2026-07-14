package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.audit.security.entity.AuditLog;
import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.identity.entity.User;
import com.hms.identity.password.repository.PasswordResetTokenRepository;
import com.hms.identity.repository.PermissionRepository;
import com.hms.identity.repository.RoleRepository;
import com.hms.identity.repository.UserRepository;

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
class SecurityEventIntegrationTest
        extends BaseIntegrationTest {
	
	@Autowired
	AuditLogRepository auditRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordResetTokenRepository resetRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PermissionRepository permissionRepository;
	
	
	@Test
	void shouldPublishLoginSuccessEvent()
	        throws Exception {

	    obtainAdminToken();

	    AuditLog audit =
	            findAudit("LOGIN_SUCCESS");

	    assertEquals(
	            "LOGIN_SUCCESS",
	            audit.getAction());

	    assertEquals(
	            "admin",
	            audit.getUsername());
	}
	
	@Test
	void shouldPublishLoginFailureEvent()
	        throws Exception {

	    mockMvc.perform(

	            post("/auth/login")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "username":"admin",
	                        "password":"wrong"
	                    }
	                    """))

	            .andExpect(status().isUnauthorized());

	    AuditLog audit =
	            findAudit("LOGIN_FAILED");

	    assertEquals(
	            "LOGIN_FAILED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishPasswordChangedEvent()
	        throws Exception {

	    String token =
	            obtainAdminToken();

	    mockMvc.perform(

	            post("/api/v1/password/change")

	                    .header(
	                            "Authorization",
	                            "Bearer " + token)

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "currentPassword":"password",
	                        "newPassword":"Password@12345",
	                        "confirmPassword":"Password@12345"
	                    }
	                    """))

	            .andExpect(status().isNoContent());

	    AuditLog audit =
	            findAudit("PASSWORD_CHANGED");

	    assertEquals(
	            "PASSWORD_CHANGED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishPasswordResetRequested()
	        throws Exception {

	    mockMvc.perform(

	            post("/auth/forgot-password")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "email":"admin@hms.com"
	                    }
	                    """))

	            .andExpect(status().isNoContent());

	    AuditLog audit =
	            findAudit("PASSWORD_RESET_REQUESTED");

	    assertEquals(
	            "PASSWORD_RESET_REQUESTED",
	            audit.getAction());
	    
	}
	
	@Test
	void shouldPublishPasswordResetCompleted()
	        throws Exception {

	    mockMvc.perform(

	            post("/auth/forgot-password")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "email":"admin@hms.com"
	                    }
	                    """));

	    String token =
	            resetRepository.findAll()

	                    .getFirst()

	                    .getToken();

	    mockMvc.perform(

	            post("/auth/reset-password")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "token":"%s",
	                        "newPassword":"Password@12345",
	                        "confirmPassword":"Password@12345"
	                    }
	                    """.formatted(token)))

	            .andExpect(status().isNoContent());

	    AuditLog audit =
	            findAudit("PASSWORD_RESET");

	    assertEquals(
	            "PASSWORD_RESET",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishAccountLocked()
	        throws Exception {

	    for (int i = 0; i < 5; i++) {

	        mockMvc.perform(

	                post("/auth/login")

	                        .contentType(MediaType.APPLICATION_JSON)

	                        .content("""
	                        {
	                            "username":"admin",
	                            "password":"wrong"
	                        }
	                        """));
	    }

	    AuditLog audit =
	            findAudit("ACCOUNT_LOCKED");

	    assertEquals(
	            "ACCOUNT_LOCKED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishAdministratorUnlock()
	        throws Exception {

	    lockAdminAccount();
	    

	    String securityToken =
	            obtainSecurityAdminToken();

	    UUID userId =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow()
	                    .getId();

	    mockMvc.perform(

	            post("/api/v1/security/users/{id}/unlock", userId)

	                    .header(
	                            "Authorization",
	                            "Bearer " + securityToken))

	            .andExpect(status().isNoContent());

	    AuditLog audit =
	            findAudit("ACCOUNT_UNLOCKED");

	    assertEquals(
	            "ACCOUNT_UNLOCKED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishAutomaticUnlock()
	        throws Exception {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    user.setAccountLocked(true);

	    user.setLockExpiresAt(
	            LocalDateTime.now().minusMinutes(5));

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

	            .andExpect(status().isOk());

	    AuditLog audit =
	            findAudit("ACCOUNT_AUTO_UNLOCKED");

	    assertEquals(
	            "ACCOUNT_AUTO_UNLOCKED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishRefreshTokenRevoked()
	        throws Exception {

        JsonNode auth = authenticateAndReturnTokens("admin", "password");

        String refreshToken =
                auth.get("refreshToken").asText();

	    mockMvc.perform(

	            post("/auth/logout")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "refreshToken":"%s"
	                    }
	                    """.formatted(refreshToken)))

	            .andExpect(status().isNoContent());

	    AuditLog audit =
	            findAudit("REFRESH_TOKEN_REVOKED");

	    assertEquals(
	            "REFRESH_TOKEN_REVOKED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishPermissionAssigned()
	        throws Exception {

	    String token =
	            obtainAdminToken();

	    UUID roleId =
	            roleRepository.findByName("ADMIN")
	                    .orElseThrow()
	                    .getId();

	    UUID permissionId =
	            permissionRepository.findByCode("USER_VIEW")
	                    .orElseThrow()
	                    .getId();

	    mockMvc.perform(

	            post("/api/v1/roles/%s/permissions/%s"
	                    .formatted(roleId, permissionId))

	                    .header(
	                            "Authorization",
	                            "Bearer " + token))

	            .andExpect(status().isOk());

	    AuditLog audit =
	            findAudit("PERMISSION_ASSIGNED");

	    assertEquals(
	            "PERMISSION_ASSIGNED",
	            audit.getAction());
	}
	
	@Test
	void shouldPublishRoleAssigned()
	        throws Exception {

	    String token =
	            obtainAdminToken();

	    UUID userId =
	            userRepository.findByUsername("admin")
	                    .orElseThrow()
	                    .getId();

	    UUID roleId =
	            roleRepository.findByName("ADMIN")
	                    .orElseThrow()
	                    .getId();

	    mockMvc.perform(

	            post("/api/v1/users/%s/roles/%s"
	                    .formatted(userId, roleId))

	                    .header(
	                            "Authorization",
	                            "Bearer " + token))

	            .andExpect(status().isOk());

	    AuditLog audit =
	            findAudit("ROLE_ASSIGNED");

	    assertEquals(
	            "ROLE_ASSIGNED",
	            audit.getAction());
	}
	
	@Test
	void shouldPersistCompleteAuditInformation()
	        throws Exception {

	    obtainAdminToken();

	    AuditLog audit =
	            findAudit("LOGIN_SUCCESS");

	    assertEquals(
	            "admin",
	            audit.getUsername());

	    assertEquals(
	            "IDENTITY",
	            audit.getModule());

	    assertEquals(
	            "USER",
	            audit.getEntityName());

	    assertNotNull(
	            audit.getCreatedAt());

	    assertNotNull(
	            audit.getEntityId());
	}
	
	private AuditLog findAudit(String action) {

	    return auditRepository.findAll()

	            .stream()

	            .filter(a -> action.equals(a.getAction()))

	            .max(
	                Comparator.comparing(
	                    AuditLog::getCreatedAt))
	            .orElseThrow();
	}
	
	private void lockAdminAccount() {
        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);
        user.setFailedLoginAttempts(5);
        user.setLockedAt(LocalDateTime.now().minusMinutes(45));
        user.setLockExpiresAt(LocalDateTime.now().minusMinutes(15));
	}
}

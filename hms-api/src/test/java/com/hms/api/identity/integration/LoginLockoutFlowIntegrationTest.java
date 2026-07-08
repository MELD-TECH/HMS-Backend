package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.audit.entity.AuditLog;
import com.hms.identity.audit.repository.AuditLogRepository;
import com.hms.identity.entity.User;
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
class LoginLockoutFlowIntegrationTest
        extends BaseIntegrationTest {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditRepository;
    
    @Test
    void shouldLockAccountAfterMaximumFailures()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {

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

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertTrue(user.getAccountLocked());

        assertEquals(
                Integer.valueOf(MAX_FAILED_ATTEMPTS),
                user.getFailedLoginAttempts());

        assertTrue(
                user.getLockedAt() != null);

        assertTrue(
                user.getLockExpiresAt() != null);
    }
    
    @Test
    void lockedUserCannotLogin()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {

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

        mockMvc.perform(

                post("/auth/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "username":"admin",
                            "password":"password"
                        }
                        """))

                .andExpect(status().isLocked());
    }
    
    @Test
    void administratorCanUnlockAccount()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {

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

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        String adminToken =
        		obtainSecurityAdminToken();

        mockMvc.perform(

                post("/api/v1/security/users/"
                		+ user.getId()
                		+ "/unlock")

                        .header(
                                "Authorization",
                                "Bearer " + adminToken)

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "userId":"%s"
                        }
                        """.formatted(user.getId())))

                .andExpect(status().isNoContent());

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertFalse(updated.getAccountLocked());
    }
    
    @Test
    void shouldResetFailedAttemptsAfterUnlock()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {

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

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        String adminToken =
        		obtainSecurityAdminToken();

        mockMvc.perform(

                post("/api/v1/security/users/"
                		+ user.getId()
                		+ "/unlock")

                        .header(
                                "Authorization",
                                "Bearer " + adminToken)

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "userId":"%s"
                        }
                        """.formatted(user.getId())));

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(0),
                updated.getFailedLoginAttempts());

        assertFalse(updated.getAccountLocked());

        assertEquals(
                null,
                updated.getLockedAt());

        assertEquals(
                null,
                updated.getLockExpiresAt());
    }
    
    @Test
    void shouldAuditAccountLocked()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {

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
                auditRepository

                        .findAll()

                        .stream()

                        .filter(a ->
                                "ACCOUNT_LOCKED"
                                        .equals(a.getAction()))

                        .findFirst()

                        .orElseThrow();

        assertEquals(
                "ACCOUNT_LOCKED",
                audit.getAction());
    }
    
    @Test
    void shouldAuditAccountUnlocked()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS; i++) {

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

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        String adminToken =
        		obtainSecurityAdminToken();

        mockMvc.perform(

                post("/api/v1/security/users/"
                		+ user.getId()
                		+ "/unlock")

                        .header(
                                "Authorization",
                                "Bearer " + adminToken)

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "userId":"%s"
                        }
                        """.formatted(user.getId())));

        AuditLog audit =
                auditRepository
                        .findTopByActionOrderByCreatedAtDesc(
                                "ACCOUNT_UNLOCKED")
                        .orElseThrow();

        assertEquals(
                "ACCOUNT_UNLOCKED",
                audit.getAction());
    }
    
    @Test
    void shouldNotIncrementCounterAfterAccountIsAlreadyLocked()
            throws Exception {

        for (int i = 0; i < MAX_FAILED_ATTEMPTS + 3; i++) {

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

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(MAX_FAILED_ATTEMPTS),
                user.getFailedLoginAttempts());
    }
    
    
}
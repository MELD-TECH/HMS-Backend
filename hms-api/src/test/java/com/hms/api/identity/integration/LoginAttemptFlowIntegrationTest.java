package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.audit.security.entity.AuditLog;
import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;

@ActiveProfiles("test")
@Sql(
        scripts = {
                "/db/testdata/001_cleanup.sql",
                "/db/testdata/002_admin_user.sql",
                "/db/testdata/003_roles.sql",
                "/db/testdata/004_role_permissions.sql",
                "/db/testdata/005_password_history.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class LoginAttemptFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditRepository;
    
    @Test
    void shouldIncrementFailedAttempts()
            throws Exception {

        mockMvc.perform(

                post("/auth/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "username":"admin",
                            "password":"wrong-password"
                        }
                        """))

                .andExpect(status().isUnauthorized());

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(1),
                user.getFailedLoginAttempts());

    }
    
    @Test
    void shouldIncrementAttemptsMultipleTimes()
            throws Exception {

        for (int i = 0; i < 3; i++) {

            mockMvc.perform(

                    post("/auth/login")

                            .contentType(MediaType.APPLICATION_JSON)

                            .content("""
                            {
                                "username":"admin",
                                "password":"wrong-password"
                            }
                            """))

                    .andExpect(status().isUnauthorized());

        }

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(3),
                user.getFailedLoginAttempts());

    }
    
    @Test
    void shouldResetAttemptsAfterSuccessfulLogin()
            throws Exception {

        /*
         * Two failures
         */

        for (int i = 0; i < 2; i++) {

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

        /*
         * Successful login
         */

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

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(0),
                user.getFailedLoginAttempts());

    }
    
    @Test
    void shouldAuditFailedLogin()
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
                auditRepository
                        .findAll()
                        .stream()

                        .filter(a ->
                                "LOGIN_FAILED"
                                        .equals(a.getAction()))

                        .findFirst()

                        .orElseThrow();

        assertEquals(
                "LOGIN_FAILED",
                audit.getAction());

    }
    
    @Test
    void shouldAuditSuccessfulLogin()
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
                "LOGIN_SUCCESS",
                audit.getAction());

    }
    
    @Test
    void shouldIncrementOnlyRequestedUser()
            throws Exception {

        mockMvc.perform(

                post("/auth/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "username":"admin",
                            "password":"wrong"
                        }
                        """));

        User admin =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(1),
                admin.getFailedLoginAttempts());

    }
    
    @Test
    void shouldRejectUnknownUser()
            throws Exception {

        mockMvc.perform(

                post("/auth/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "username":"unknown",
                            "password":"password"
                        }
                        """))

                .andExpect(status().isUnauthorized());

    }
    
    @Test
    void shouldContinueIncrementingFailures()
            throws Exception {

        for (int i = 1; i <= 4; i++) {

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
                Integer.valueOf(4),
                user.getFailedLoginAttempts());

    }
    
    
    
}
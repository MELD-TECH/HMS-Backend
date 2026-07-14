package com.hms.api.identity.integration;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.authentication.entity.PendingAuthentication;
import com.hms.identity.authentication.repository.PendingAuthenticationRepository;
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
                "/db/testdata/007_mfa_admin.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class MfaAuthenticationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingAuthenticationRepository pendingRepository;

    private User admin() {

        return userRepository

                .findByUsername("admin")

                .orElseThrow();

    }
    
    private PendingAuthentication pending() {

        UUID userId = admin().getId();

        return pendingRepository

                .findByUserId(userId)

                .orElseThrow();

    }
    
    
    @Test
    void shouldReturnChallengeTokenWhenMfaEnabled()
            throws Exception {

        JsonNode response = login("admin", "password");

        assertTrue(
                response.get("mfaRequired").asBoolean());

        assertEquals(
                "EMAIL",
                response.get("mfaType").asText());

        assertTrue(
                response.hasNonNull("challengeToken"));

        assertTrue(
                response.hasNonNull("expiresAt"));

        assertTrue(
                response.get("accessToken").isNull());

        assertTrue(
                response.get("refreshToken").isNull());

        assertTrue(
                response.get("sessionId").isNull());

        assertTrue(
                response.get("tokenType").isNull());
    }
    
    @Test
    void shouldCreatePendingAuthentication()
            throws Exception {

    	loginResponse();

        PendingAuthentication pending = pending();

        assertEquals(

                admin().getId(),

                pending.getUserId());

        assertEquals(

                "admin",

                pending.getUsername());

        assertNotNull(

                pending.getChallengeToken());

        assertNotNull(

                pending.getExpiresAt());

        assertNull(

                pending.getCompletedAt());
    }
    
    @Test
    void shouldStoreClientIp()
            throws Exception {

    	loginResponse();

        PendingAuthentication pending = pending();

        assertEquals(

                "127.0.0.1",

                pending.getIpAddress());
    }
    
    @Test
    void shouldStoreUserAgent()
            throws Exception {

        mockMvc.perform(

                post("/auth/login")

                        .header(

                                "User-Agent",

                                "JUnit-Test")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "username":"admin",
                            "password":"password"
                        }
                        """))

                .andExpect(status().isOk());

        PendingAuthentication pending = pending();

        assertEquals(

                "JUnit-Test",

                pending.getUserAgent());
    }
    
    @Test
    void shouldGenerateUniqueChallengeTokens()
            throws Exception {

        JsonNode firstAuth = login("admin", "password");

        String firstToken =
        		firstAuth.get("challengeToken").asText();

        pendingRepository.deleteAll();

        JsonNode secondAuth = login("admin", "password");

        String secondToken =
        		secondAuth.get("challengeToken").asText();

        assertNotEquals(

                firstToken,

                secondToken);
    }
    
    
    
    
}
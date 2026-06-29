package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.session.entity.RefreshToken;
import com.hms.identity.session.repository.RefreshTokenRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/V9999__test_admin_user.sql"
    }
)
class SessionFlowIntegrationTest
        extends BaseIntegrationTest {
	
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldListSessions()
            throws Exception {

        String token =
                obtainAdminToken();

        mockMvc.perform(

                get("/api/v1/sessions")

                        .header(
                                "Authorization",
                                "Bearer " + token)

        )

        .andExpect(status().isOk());

    }
    
    @Test
    void shouldLoginAndReturnTokens()
            throws Exception {

        String response = login();

        JsonNode json =
                objectMapper.readTree(response);

        assertTrue(
                json.has("accessToken")
        );

        assertTrue(
                json.has("refreshToken")
        );

        assertEquals(
                "Bearer",
                json.get("tokenType").asText()
        );
    }
    
    @Test
    void shouldRefreshAccessToken()
            throws Exception {

        String login =
                login();

        JsonNode json =
                objectMapper.readTree(login);

        String refresh =
                json.get("refreshToken").asText();

        String oldAccess =
                json.get("accessToken").asText();

        String refreshed =
                mockMvc.perform(

                        post("/auth/refresh")

                                .contentType(
                                        MediaType.APPLICATION_JSON)

                                .content("""
                                {
                                    "refreshToken":"%s"
                                }
                                """.formatted(refresh))
                )

                .andExpect(status().isOk())

                .andReturn()

                .getResponse()

                .getContentAsString();

        JsonNode refreshJson =
                objectMapper.readTree(refreshed);

        String newAccess =
                refreshJson.get("accessToken").asText();

        assertNotEquals(
                oldAccess,
                newAccess
        );
    }
    
    @Test
    void shouldRotateRefreshToken()
            throws Exception {

        String login =
                login();

        JsonNode json =
                objectMapper.readTree(login);

        String refresh =
                json.get("refreshToken").asText();

        String refreshed =
                mockMvc.perform(

                        post("/auth/refresh")

                                .contentType(
                                        MediaType.APPLICATION_JSON)

                                .content("""
                                {
                                    "refreshToken":"%s"
                                }
                                """.formatted(refresh))
                )

                .andExpect(status().isOk())

                .andReturn()

                .getResponse()

                .getContentAsString();

        JsonNode rotated =
                objectMapper.readTree(refreshed);

        assertNotEquals(
                refresh,
                rotated.get("refreshToken").asText()
        );
    }
    
    @Test
    void oldRefreshTokenShouldFail()
            throws Exception {

        String login =
                login();

        JsonNode json =
                objectMapper.readTree(login);

        String refresh =
                json.get("refreshToken").asText();

        mockMvc.perform(

                post("/auth/refresh")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                          "refreshToken":"%s"
                        }
                        """.formatted(refresh))
        );

        mockMvc.perform(

                post("/auth/refresh")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content("""
                        {
                          "refreshToken":"%s"
                        }
                        """.formatted(refresh))
        )

        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void shouldListActiveSessions()
            throws Exception {

        String login = login();

        String token =
                objectMapper.readTree(login)
                        .get("accessToken")
                        .asText();

        mockMvc.perform(

                get("/api/v1/sessions")

                        .header(
                                "Authorization",
                                "Bearer " + token)
        )

        .andExpect(status().isOk())

        .andExpect(jsonPath("$").isArray())

        .andExpect(jsonPath("$[0].deviceName").exists())

        .andExpect(jsonPath("$[0].ipAddress").exists());
    }
    
    @Test
    void shouldRevokeSingleSession()
            throws Exception {

        String login =
                login();

        JsonNode json =
                objectMapper.readTree(login);

        String token =
                json.get("accessToken").asText();

        UUID sessionId =
                UUID.fromString(
                        json.get("sessionId").asText());

        mockMvc.perform(

                delete("/api/v1/sessions/{id}", sessionId)

                        .header(
                                "Authorization",
                                "Bearer " + token)
        )

        .andExpect(status().isNoContent());
    }
    

    @Test
    void shouldRejectExpiredRefreshToken()
            throws Exception {

        String login =
                login();

        JsonNode json =
                objectMapper.readTree(login);

        String refreshToken =
                json.get("refreshToken")
                    .asText();

        RefreshToken token =
                refreshTokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow();

        token.setExpiresAt(
                LocalDateTime.now()
                        .minusMinutes(5));

        refreshTokenRepository.expireToken(
                refreshToken,
                LocalDateTime.now().minusMinutes(5));

        mockMvc.perform(

                post("/auth/refresh")

                        .contentType(
                                MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "refreshToken":"%s"
                        }
                        """.formatted(refreshToken))
        )

        .andExpect(status().isUnauthorized());
    }
}

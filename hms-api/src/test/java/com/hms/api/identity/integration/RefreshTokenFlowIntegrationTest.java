package com.hms.api.identity.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.api.test.BaseIntegrationTest;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/V9999__test_admin_user.sql"
    }
)
class RefreshTokenFlowIntegrationTest
        extends BaseIntegrationTest {

    @Test
    void shouldRefreshAccessToken()
            throws Exception {

        String loginResponse =
                mockMvc.perform(

                        post("/auth/login")

                                .contentType(
                                        MediaType.APPLICATION_JSON)

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

        JsonNode auth = authenticateAndReturnTokens("admin", "password");

        String refreshToken =
                auth.get("refreshToken").asText();
        
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

        .andExpect(status().isOk())

        .andExpect(jsonPath(
                "$.accessToken").exists())

        .andExpect(jsonPath(
                "$.refreshToken").exists());

    }

}

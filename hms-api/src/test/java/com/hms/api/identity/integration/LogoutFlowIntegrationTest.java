package com.hms.api.identity.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class LogoutFlowIntegrationTest
        extends BaseIntegrationTest {

    @Test
    void shouldLogout()
            throws Exception {

        JsonNode auth = authenticateAndReturnTokens("admin", "password");

        String refreshToken =
                auth.get("refreshToken").asText();

        mockMvc.perform(

                post("/auth/logout")

                        .contentType(
                                MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "refreshToken":"%s"
                        }
                        """.formatted(refreshToken))

        )

        .andExpect(status().isNoContent());

    }
    
    @Test
    void refreshAfterLogoutShouldFail()
            throws Exception {

        JsonNode auth = authenticateAndReturnTokens("admin", "password");

        String refreshToken =
                auth.get("refreshToken").asText();
        mockMvc.perform(

                post("/auth/logout")

                        .contentType(
                                MediaType.APPLICATION_JSON)

                        .content("""
                        {
                            "refreshToken":"%s"
                        }
                        """.formatted(refreshToken))
        );

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
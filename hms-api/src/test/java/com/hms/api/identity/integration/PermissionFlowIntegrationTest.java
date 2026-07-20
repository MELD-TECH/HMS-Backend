package com.hms.api.identity.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/V9999__test_admin_user.sql"
    }
)
class PermissionFlowIntegrationTest
        extends BaseIntegrationTest {

    @Test
    void shouldCreatePermission()
            throws Exception {

        String token =
                obtainAdminToken();

        mockMvc.perform(
                post("/api/v1/permissions")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("""
                        {
                            "code":"DISPENSE_MEDICATION",
                            "description":"Dispense Medication"
                        }
                        """)
        )
        .andExpect(status().isOk())
        .andExpect(
                jsonPath("$.code")
                        .value("DISPENSE_MEDICATION")
        );
    }

    @Test
    void shouldListPermissions()
            throws Exception {

        String token =
                obtainAdminToken();

        mockMvc.perform(
                get("/api/v1/permissions")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
        )
        .andExpect(status().isOk());
    }
}
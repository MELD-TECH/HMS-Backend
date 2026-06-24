package com.hms.api.identity.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class RoleFlowIntegrationTest extends BaseIntegrationTest {

@Test
void shouldCreateRole()
    throws Exception {

String token =
        obtainAdminToken();

mockMvc.perform(
        post("/api/v1/roles")
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .content("""
                    {
                      "name":"LAB_MANAGER",
                      "description":"Lab Manager"
                    }
                """)
)
.andExpect(status().isOk());
}
}

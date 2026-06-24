package com.hms.api.identity.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;


@ActiveProfiles("test")
@Sql(
	    scripts = {
	     "/db/testdata/V9999__test_admin_user.sql"			 
	    }
	)
class SecurityFlowIntegrationTest extends BaseIntegrationTest {

@Test
void anonymousShouldFail()
    throws Exception {

mockMvc.perform(
        get("/api/v1/users")
)
.andExpect(
        status().isUnauthorized()
);
}

@Test
void authenticatedShouldPass()
    throws Exception {

String token =
        obtainAdminToken();

mockMvc.perform(
        get("/api/v1/users")
        .header(
                "Authorization",
                "Bearer " + token
        )
)
.andExpect(status().isOk());
}
}
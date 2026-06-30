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
	        "/db/testdata/001_cleanup.sql",
	        "/db/testdata/002_admin_user.sql",
	        "/db/testdata/003_roles.sql",
	        "/db/testdata/004_role_permissions.sql",
	        "/db/testdata/005_password_history.sql"
	    },
	    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
public class PasswordFlowIntegrationTest extends BaseIntegrationTest {

	@Test
	void shouldChangePassword()
	        throws Exception {

	    String token =
	            obtainAdminToken();

	    mockMvc.perform(

	            post("/api/v1/password/change")

	                    .header(
	                            "Authorization",
	                            "Bearer " + token)

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "currentPassword":"password",
	                        "newPassword":"Password@12345",
	                        "confirmPassword":"Password@12345"
	                    }
	                    """))

	            .andExpect(status().isNoContent());

	}
	
	@Test
	void shouldRejectWrongCurrentPassword()
	        throws Exception {

	    String token =
	            obtainAdminToken();

	    mockMvc.perform(

	            post("/api/v1/password/change")

	                    .header(
	                            "Authorization",
	                            "Bearer " + token)

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "currentPassword":"wrong",
	                        "newPassword":"Password@12345",
	                        "confirmPassword":"Password@12345"
	                    }
	                    """))

	            .andExpect(status().isBadRequest());

	}
	
	@Test
	void shouldRejectSamePassword()
	        throws Exception {

	    String token =
	            obtainAdminToken();

	    mockMvc.perform(

	            post("/api/v1/password/change")

	                    .header(
	                            "Authorization",
	                            "Bearer " + token)

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "currentPassword":"password",
	                        "newPassword":"password",
	                        "confirmPassword":"password"
	                    }
	                    """))

	            .andExpect(status().isBadRequest());

	}
	
	@Test
	void shouldRejectPasswordReuse()
	        throws Exception {

	    /*
	     * Login with original password
	     */
	    String token =
	            obtainAdminToken();

	    /*
	     * Change password
	     */
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

	    /*
	     * Login again using the new password
	     */
	    String loginResponse =

	            mockMvc.perform(

	                    post("/auth/login")

	                            .contentType(MediaType.APPLICATION_JSON)

	                            .content("""
	                            {
	                                "username":"admin",
	                                "password":"Password@12345"
	                            }
	                            """))

	                    .andExpect(status().isOk())

	                    .andReturn()

	                    .getResponse()

	                    .getContentAsString();

	    JsonNode loginJson =
	            objectMapper.readTree(loginResponse);

	    String newToken =
	            loginJson
	                    .get("accessToken")
	                    .asText();

	    /*
	     * Try to reuse old password
	     */
	    mockMvc.perform(

	            post("/api/v1/password/change")

	                    .header(
	                            "Authorization",
	                            "Bearer " + newToken)

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "currentPassword":"Password@12345",
	                        "newPassword":"password",
	                        "confirmPassword":"password"
	                    }
	                    """))

	            .andExpect(status().isBadRequest());
	}
}

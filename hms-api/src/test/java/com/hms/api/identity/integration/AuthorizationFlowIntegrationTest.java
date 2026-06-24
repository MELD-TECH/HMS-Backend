package com.hms.api.identity.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/V9999__test_admin_user.sql"
    }
)
public class AuthorizationFlowIntegrationTest  extends BaseIntegrationTest {

	
	@Test
	@WithMockUser(
	        username = "admin",
	        authorities = {
	                "USER_CREATE"
	        }
	)
	void userCreatePermissionShouldPass()
	        throws Exception {

		mockMvc.perform(
	            post("/api/v1/users")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("""
	            {
	                "username":"doctor20",
	                "email":"doctor20@hms.com",
	                "password":"password",
	                "firstName":"John",
	                "lastName":"Doe"
	            }
	            """)
	    )
	    .andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(
	        username = "viewer",
	        authorities = {
	                "USER_VIEW"
	        }
	)
	void userViewShouldNotCreateUser()
	        throws Exception {

	    mockMvc.perform(
	            post("/api/v1/users")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("""
	            {
	                "username":"doctor21",
	                "email":"doctor21@hms.com",
	                "password":"password",
	                "firstName":"John",
	                "lastName":"Doe"
	            }
	            """)
	    )
	    .andExpect(status().isForbidden());
	}
	
	
	@Test
	@WithMockUser(
	        username = "roleadmin",
	        authorities = {
	                "ROLE_CREATE"
	        }
	)
	void roleCreateShouldNotCreateUser()
	        throws Exception {

	    mockMvc.perform(
	            post("/api/v1/users")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content("""
	            {
	                "username":"doctor22",
	                "email":"doctor22@hms.com",
	                "password":"password",
	                "firstName":"John",
	                "lastName":"Doe"
	            }
	            """)
	    )
	    .andExpect(status().isForbidden());
	}
	
	@Test
	void anonymousShouldBeRejected()
	        throws Exception {

	    mockMvc.perform(
	            get("/api/v1/users")
	    )
	    .andExpect(status().isUnauthorized());
	}
}

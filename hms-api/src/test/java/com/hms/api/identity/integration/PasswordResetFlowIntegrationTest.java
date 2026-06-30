package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.password.entity.PasswordResetToken;
import com.hms.identity.password.repository.PasswordResetTokenRepository;

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
public class PasswordResetFlowIntegrationTest extends BaseIntegrationTest {

	@Autowired
	PasswordResetTokenRepository repository;
	
	@Test
	void shouldGenerateResetToken()
	        throws Exception {

	    mockMvc.perform(

	            post("/auth/forgot-password")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "email":"admin@hms.com"
	                    }
	                    """))

	            .andExpect(status().isNoContent());

	    assertEquals(
	            1,
	            repository.count());
	}
	
	@Test
	void shouldIgnoreUnknownEmail()
	        throws Exception {

	    mockMvc.perform(

	            post("/auth/forgot-password")

	                    .contentType(MediaType.APPLICATION_JSON)

	                    .content("""
	                    {
	                        "email":"unknown@hms.com"
	                    }
	                    """))

	            .andExpect(status().isNoContent());

	    assertEquals(
	            0,
	            repository.count());
	}
	
	@Test
	void shouldExpirePreviousResetTokens()
	        throws Exception {

	    mockMvc.perform(
	            post("/auth/forgot-password")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content("""
	                    {
	                        "email":"admin@hms.com"
	                    }
	                    """))
	            .andExpect(status().isNoContent());

	    mockMvc.perform(
	            post("/auth/forgot-password")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content("""
	                    {
	                        "email":"admin@hms.com"
	                    }
	                    """))
	            .andExpect(status().isNoContent());

	    List<PasswordResetToken> tokens =
	            repository.findAll();

	    assertEquals(2, tokens.size());

	    long active =
	            tokens.stream()
	                  .filter(t -> !t.isUsed())
	                  .count();

	    assertEquals(1, active);
	}
	
	
}

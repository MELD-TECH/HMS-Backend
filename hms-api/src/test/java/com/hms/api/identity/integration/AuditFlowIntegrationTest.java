package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.audit.entity.AuditLog;
import com.hms.identity.audit.repository.AuditLogRepository;

@ActiveProfiles("test")
@Sql(
	    scripts = {
	     "/db/testdata/V9999__test_admin_user.sql"			 
	    }
	)
class AuditFlowIntegrationTest extends BaseIntegrationTest {

@Autowired
AuditLogRepository repository;

@Test
void shouldWriteAuditRecord()
    throws Exception {

String token =
        obtainAdminToken();

mockMvc.perform(
	    post("/api/v1/roles")
	        .header(
	            "Authorization",
	            "Bearer " + token
	        )
	        .contentType(MediaType.APPLICATION_JSON)
	        .content("""
	        {
	          "name":"AUDIT_TEST_ROLE",
	          "description":"Audit Test"
	        }
	        """)
	)
	.andExpect(status().isOk());

	AuditLog log =
			repository.findFirstByAction("ROLE_CREATED")
							.orElseThrow();

	assertEquals(
			"ROLE_CREATED",
			log.getAction()
			);

	assertTrue(
	    repository.count() > 0
			);
	
	assertEquals(
	        "admin",
	        log.getUsername()
	);

	assertEquals(
	        "admin",
	        log.getUsername()
	);
	
	}


}
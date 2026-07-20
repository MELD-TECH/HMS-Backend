package com.hms.api.patient.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.hms.api.patient.support.PatientTestDataFactory;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.patient.entity.Patient;
import com.hms.patient.repository.PatientRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/003_roles.sql",
        "/db/testdata/004_role_permissions.sql",
        "/db/testdata/005_password_history.sql",
        "/db/testdata/006_security_admin.sql",
        "/db/testdata/007_mfa_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Transactional
class PatientSearchIntegrationTest
        extends BaseIntegrationTest {

	@Autowired
	private PatientRepository repository;
	
	private Patient john;
	
	private Patient jane;
	
	private Patient archived;
	
	private Patient deceased;
	
	private Patient inactive;
	
	@BeforeEach
	void setup() {

		john =
		        repository.save(
		                PatientTestDataFactory.activePatient());

		jane =
		        repository.save(
		                PatientTestDataFactory.secondPatient());

		archived =
		        repository.save(
		                PatientTestDataFactory.archivedPatient());

		deceased =
		        repository.save(
		                PatientTestDataFactory.deceasedPatient());
		
	    inactive = repository.save(
	            PatientTestDataFactory.inactivePatient());
	}
	
	private ResultActions search(String query)
	        throws Exception {

	    return mockMvc.perform(

	            get("/api/v1/patients?" + query)

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainAdminToken()));
	}
	
	@Test
	void shouldReturnPatients()
	        throws Exception {

	    search("")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content").isArray())

	            .andExpect(jsonPath("$.content.length()")
	                    .value(5));
	}
	
	@Test
	void shouldSearchByFullName()
	        throws Exception {

	    search("firstName=John")

	    .andExpect(jsonPath("$.content[*].fullName",
	            org.hamcrest.Matchers.hasItem(john.getFirstName()
	                    + " "
	                    + john.getMiddleName()
	                    + " "
	                    + john.getLastName())));
	}
	
	@Test
	void shouldSearchByLastName()
	        throws Exception {

	    search("lastName=Doe")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[0].fullName")
	                    .value(john.getFirstName() + " " + john.getMiddleName() + " " + john.getLastName()));
	}
	
	@Test
	void shouldSearchByPatientNumber()
	        throws Exception {

		search("patientNumber=" + john.getPatientNumber())

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[0].patientNumber")
	                    .value(john.getPatientNumber()));
	}
	
	@Test
	void shouldSearchByPhoneNumber()
	        throws Exception {
		

		search("phoneNumber=" + john.getPhoneNumber())

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[0].phoneNumber")
	                    .value(john.getPhoneNumber()));
	}
	
	@Test
	void shouldSearchByEmail()
	        throws Exception {
		

		search("email=" + john.getEmail())

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[0].patientNumber")
	                    .value(john.getPatientNumber()));
	}
	
	@Test
	void shouldSearchActivePatients()
	        throws Exception {

	    search("status=ACTIVE")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[*].status")
	                    .value(org.hamcrest.Matchers.everyItem(
	                            org.hamcrest.Matchers.is("ACTIVE"))));
	}
	
	@Test
	void shouldSearchArchivedPatients()
	        throws Exception {

	    search("status=ARCHIVED")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[0].status")
	                    .value("ARCHIVED"));
	}
	
	@Test
	void shouldSearchDeceasedPatients()
	        throws Exception {

	    search("status=DECEASED")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content[0].status")
	                    .value("DECEASED"));
	}
	
	@Test
	void shouldReturnPagedResults()
	        throws Exception {

	    search("page=0&size=2")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.page.size")
	                    .value(2))

	            .andExpect(jsonPath("$.page.number")
	                    .value(0))

	            .andExpect(jsonPath("$.page.totalElements")
	                    .value(5))

	            .andExpect(jsonPath("$.page.totalPages")
	                    .value(3))

	            .andExpect(jsonPath("$.content.length()")
	                    .value(2));
	}
	
	@Test
	void shouldSortAscending()
	        throws Exception {

	    search("sort=lastName,asc")

	            .andExpect(status().isOk());
	}
	
	@Test
	void shouldReturnEmptyPage()
	        throws Exception {

	    search("firstName=Unknown")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content.length()")
	                    .value(0));
	}
	
	@Test
	void shouldRejectWithoutAuthentication()
	        throws Exception {

	    mockMvc.perform(

	            get("/api/v1/patients"))

	            .andExpect(status().isUnauthorized());
	}
	
	@Test
	void shouldRejectWithoutPermission()
	        throws Exception {

	    mockMvc.perform(

	            get("/api/v1/patients")

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainSecurityAdminToken()))

	            .andExpect(status().isForbidden());
	}
	
	@Test
	void shouldSearchUsingMultipleCriteria()
	        throws Exception {

	    search("firstName=John&status=ACTIVE")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content.length()")
	                    .value(1));
	}
	
	@Test
	void shouldSearchIgnoringCase()
	        throws Exception {

	    search("firstName=john")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content.length()")
	                    .value(1));
	}
	
	@Test
	void shouldSearchUsingPartialFirstName()
	        throws Exception {

	    search("firstName=Jo")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.content.length()")
	                    .value(1));
	}
	
	@Test
	void shouldSearchUsingPaginationAndSorting()
	        throws Exception {

	    search("page=0&size=3&sort=firstName,asc")

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.page.size")
	                    .value(3))

	            .andExpect(jsonPath("$.page.number")
	                    .value(0))

	            .andExpect(jsonPath("$.page.totalElements")
	                    .value(5))

	            .andExpect(jsonPath("$.page.totalPages")
	                    .value(2))

	            .andExpect(jsonPath("$.content.length()")
	                    .value(3));
	}
	
	
}

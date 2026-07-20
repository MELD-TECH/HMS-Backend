package com.hms.api.patient.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.startsWith;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.repository.PatientRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/003_roles.sql",
        "/db/testdata/004_role_permissions.sql",
        "/db/testdata/005_password_history.sql",
        "/db/testdata/007_mfa_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class PatientRegistrationIntegrationTest extends BaseIntegrationTest  {

	@Autowired
	private PatientRepository repository;

	@Autowired
	private ObjectMapper objectMapper;
	
	private String validRequest() {

	    return """
	    {
	      "firstName":"John",
	      "middleName":"Michael",
	      "lastName":"Doe",
	      "dateOfBirth":"1995-05-20",
	      "gender":"MALE",
	      "maritalStatus":"SINGLE",
	      "bloodGroup":"O_POSITIVE",
	      "genotype":"AA",
	      "email":"john@test.com",
	      "phoneNumber":"08012345678"
	    }
	    """;
	}
	
	private ResultActions registerPatient()
	        throws Exception {

	    return mockMvc.perform(

	            post("/api/v1/patients")

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainAdminToken())

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(validRequest()));
	}
	
	@Test
	void shouldRegisterPatient()
	        throws Exception {

	    registerPatient()

	            .andExpect(status().isCreated())

	            .andExpect(jsonPath("$.patientNumber").exists())

	            .andExpect(jsonPath("$.firstName")
	                    .value("John"))

	            .andExpect(jsonPath("$.lastName")
	                    .value("Doe"))

	            .andExpect(jsonPath("$.status")
	                    .value("ACTIVE"))

	            .andExpect(jsonPath("$.deceased")
	                    .value(false));
	}
	
	@Test
	void shouldPersistPatient()
	        throws Exception {

	    registerPatient();

	    Patient patient =

	            repository.findAll()

	                    .stream()

	                    .findFirst()

	                    .orElseThrow();

	    assertEquals(
	            "John",
	            patient.getFirstName());

	    assertEquals(
	            "Doe",
	            patient.getLastName());
	}
	
	@Test
	void shouldGeneratePatientNumber()
	        throws Exception {

	    registerPatient();

	    Patient patient =

	            repository.findAll()

	                    .stream()

	                    .findFirst()

	                    .orElseThrow();

	    assertTrue(

	            patient.getPatientNumber()

	                    .matches(

	                            "HMS-\\d{4}-\\d{6,}"));
	}
	
	@Test
	void shouldPersistPatientNumber()
	        throws Exception {

	    registerPatient();

	    Patient patient =

	            repository.findAll()

	                    .stream()

	                    .findFirst()

	                    .orElseThrow();

	    assertTrue(

	            repository.existsByPatientNumber(

	                    patient.getPatientNumber()));
	}
	
	@Test
	void shouldReturnFullName()
	        throws Exception {

	    registerPatient()

	            .andExpect(

	                    jsonPath("$.fullName")

	                            .value("John Michael Doe"));
	}
	
	@Test
	void shouldDefaultStatusToActive()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(

	            PatientStatus.ACTIVE,

	            patient.getStatus());
	}
	
	@Test
	void shouldDefaultDeceasedFalse()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertFalse(patient.getDeceased());
	}
	
	@Test
	void shouldPersistPhone()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(
	            "08012345678",
	            patient.getPhoneNumber());
	}
	
	@Test
	void shouldPersistEmail()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(
	            "john@test.com",
	            patient.getEmail());
	}
	
	@Test
	void shouldPersistBloodGroup()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(
	            BloodGroup.O_POSITIVE,
	            patient.getBloodGroup());
	}
	
	@Test
	void shouldPersistGenotype()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(
	            Genotype.AA,
	            patient.getGenotype());
	}
	
	@Test
	void shouldPersistGender()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(
	            Gender.MALE,
	            patient.getGender());
	}
	
	@Test
	void shouldPersistDateOfBirth()
	        throws Exception {

	    registerPatient();

	    Patient patient =
	            repository.findAll()
	                    .getFirst();

	    assertEquals(

	            LocalDate.of(1995,5,20),

	            patient.getDateOfBirth());
	}
	
	@Test
	void shouldRejectDuplicateEmail()
	        throws Exception {

	    registerPatient();

	    registerPatient()

	            .andExpect(status().isConflict());
	}
	
	@Test
	void shouldRejectFutureDateOfBirth()
	        throws Exception {

	    String request = """
	    {
	      "firstName":"John",
	      "lastName":"Doe",
	      "dateOfBirth":"2099-01-01",
	      "gender":"MALE",
	      "maritalStatus":"SINGLE",
	      "bloodGroup":"O_POSITIVE",
	      "genotype":"AA"
	    }
	    """;

	    mockMvc.perform(

	            post("/api/v1/patients")

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainAdminToken())

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(request))

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldRejectWithoutAuthentication()
	        throws Exception {

	    mockMvc.perform(

	            post("/api/v1/patients")

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(validRequest()))

	            .andExpect(status().isUnauthorized());
	}
	
	@Test
	void shouldRejectMalformedRequest()
	        throws Exception {

	    mockMvc.perform(

	            post("/api/v1/patients")

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainAdminToken())

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content("{}"))

	            .andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldReturnPatientAge()
	        throws Exception {

	    registerPatient()

	            .andExpect(

	                    jsonPath("$.age").isNumber());
	}
	
	@Test
	void shouldReturnGeneratedPatientNumber()
	        throws Exception {

	    registerPatient()

	            .andExpect(

	                    jsonPath("$.patientNumber")

	                            .value(

	                                    startsWith("HMS-")));
	}
	
	
	@Test
	void shouldRejectDuplicatePhone()
	       throws Exception {
		
		registerPatient();
		
	    String request = """
	    {
	      "firstName":"John",
	      "lastName":"Doe",
	      "dateOfBirth":"1995-05-20",
	      "gender":"MALE",
	      "maritalStatus":"SINGLE",
	      "bloodGroup":"O_POSITIVE",
	      "genotype":"AA",
	      "email":"star@gmail.com",
	      "phoneNumber":"08012345678"
	    }
	    """;

	    mockMvc.perform(

	            post("/api/v1/patients")

	                    .header(
	                            "Authorization",
	                            "Bearer " + obtainAdminToken())

	                    .contentType(
	                            MediaType.APPLICATION_JSON)

	                    .content(request))
	    
	    .andExpect(status().isConflict());
	}
	       
}

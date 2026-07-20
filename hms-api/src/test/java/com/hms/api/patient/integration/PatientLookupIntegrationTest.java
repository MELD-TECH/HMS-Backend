package com.hms.api.patient.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
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
@Transactional
class PatientLookupIntegrationTest
        extends BaseIntegrationTest {

	@Autowired
	private PatientRepository repository;
	
	private Patient patient;
	
	@BeforeEach
	void setup() {

	    patient = Patient.builder()

	            .patientNumber("PAT00000001")

	            .firstName("John")

	            .middleName("Peter")

	            .lastName("Doe")

	            .gender(Gender.MALE)

	            .dateOfBirth(LocalDate.of(1990,1,1))

	            .maritalStatus(MaritalStatus.SINGLE)

	            .bloodGroup(BloodGroup.O_POSITIVE)

	            .genotype(Genotype.AA)

	            .phoneNumber("08012345678")

	            .email("john@test.com")

	            .status(PatientStatus.ACTIVE)

	            .deceased(false)
	            
	            .build();

	    patient = repository.save(patient);

	}
	
	@Test
	void shouldLookupPatientById()
	        throws Exception {

		mockMvc.perform(

		        get("/api/v1/patients/{id}", patient.getId())

		                .header(
		                        "Authorization",
		                        "Bearer " + obtainAdminToken()))

		        .andExpect(status().isOk())

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.patientNumber")

	                    .value("PAT00000001"))

	            .andExpect(jsonPath("$.firstName")

	                    .value("John"))

	            .andExpect(jsonPath("$.lastName")

	                    .value("Doe"))

	            .andExpect(jsonPath("$.status")

	                    .value("ACTIVE"));
	}
	
	@Test
	void shouldReturn404WhenPatientNotFound()
	        throws Exception {

		mockMvc.perform(

		        get("/api/v1/patients/{id}", UUID.randomUUID())

                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))

		        .andExpect(status().isNotFound());
	}
	
	@Test
	void shouldLookupPatientByNumber()
	        throws Exception {

	    mockMvc.perform(

	            get("/api/v1/patients/number/{patientNumber}",

	                    patient.getPatientNumber())

                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))

	            .andExpect(status().isOk())

	            .andExpect(jsonPath("$.patientNumber")

	                    .value(patient.getPatientNumber()));
	}
	
	@Test
	void shouldReturn404WhenPatientNumberNotFound()
	        throws Exception {

	    mockMvc.perform(

	            get("/api/v1/patients/number/{patientNumber}",

	                    "INVALID")

                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))

	            .andExpect(status().isNotFound());
	}
	
	@Test
	void shouldRejectUnauthenticatedRequest()
	        throws Exception {

	    mockMvc.perform(

	            get("/api/v1/patients/{id}",

	                    patient.getId()))

	            .andExpect(status().isUnauthorized());
	}
	
	@Test
	void shouldRejectWithoutPermission()
	        throws Exception {

	    mockMvc.perform(

	            get("/api/v1/patients/{id}",

	                    patient.getId())

                .with(jwt()

                        .authorities()))

	            .andExpect(status().isForbidden());
	}
	
	@Test
	void shouldReturnComputedFullName() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.fullName")
	                    .value("John Peter Doe"));
	}
	
	@Test
	void shouldLookupArchivedPatient() throws Exception {

	    patient.setStatus(PatientStatus.ARCHIVED);
	    patient.setArchiveReason("Duplicate patient");
	    patient.setArchivedBy("admin");
	    patient = repository.save(patient);

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.status")
	                    .value("ARCHIVED"))
	            .andExpect(jsonPath("$.archiveReason")
	                    .value("Duplicate patient"))
	            .andExpect(jsonPath("$.archivedBy")
	                    .value("admin"));
	}
	
	@Test
	void shouldLookupDeceasedPatient() throws Exception {

	    patient.setStatus(PatientStatus.DECEASED);
	    patient.setDeceased(true);
	    patient.setDeceasedDate(LocalDate.now());
	    patient.setCauseOfDeath("Cardiac Arrest");
	    patient.setDeceasedNotes("Certified by attending physician");

	    patient = repository.save(patient);

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.status")
	                    .value("DECEASED"))
	            .andExpect(jsonPath("$.deceased")
	                    .value(true))
	            .andExpect(jsonPath("$.deceasedDate").exists())
	            .andExpect(jsonPath("$.causeOfDeath")
	                    .value("Cardiac Arrest"))
	            .andExpect(jsonPath("$.deceasedNotes")
	                    .value("Certified by attending physician"));
	}
	
	@Test
	void shouldLookupInactivePatient() throws Exception {

	    patient.setStatus(PatientStatus.INACTIVE);

	    patient = repository.save(patient);

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.status")
	                    .value("INACTIVE"));
	}
	
	@Test
	void shouldReturnAuditInformation() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.createdAt").exists())
	            .andExpect(jsonPath("$.updatedAt").exists())
	            .andExpect(jsonPath("$.createdBy").exists())
	            .andExpect(jsonPath("$.updatedBy").exists());
	}
	
	
	@Test
	void shouldLookupPatientByNumberAndReturnDetails() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/number/{patientNumber}",
	                    patient.getPatientNumber())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.patientNumber")
	                    .value(patient.getPatientNumber()))
	            .andExpect(jsonPath("$.firstName")
	                    .value("John"))
	            .andExpect(jsonPath("$.lastName")
	                    .value("Doe"))
	            .andExpect(jsonPath("$.status")
	                    .value("ACTIVE"));
	}
	
	@Test
	void shouldReturnPatientEmail() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.email")
	                    .value("john@test.com"));
	}
	
	@Test
	void shouldReturnPatientPhoneNumber() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.phoneNumber")
	                    .value("08012345678"));
	}
	
	@Test
	void shouldReturnGender() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.gender")
	                    .value("MALE"));
	}
	
	@Test
	void shouldReturnBloodGroup() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/patients/{id}", patient.getId())
                .header(
                        "Authorization",
                        "Bearer " + obtainAdminToken()))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.bloodGroup")
	                    .value("O_POSITIVE"));
	}
	
	
}

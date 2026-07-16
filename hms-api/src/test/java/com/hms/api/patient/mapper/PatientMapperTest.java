package com.hms.api.patient.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.mapper.PatientMapper;

class PatientMapperTest {

    private PatientMapper mapper;
    
    @BeforeEach
    void setUp() {

        mapper = new PatientMapper();
    }
    
    @Test
    void shouldMapCreateRequestToEntity() {

        CreatePatientRequest request =
                CreatePatientRequest.builder()
                        .firstName("John")
                        .middleName("Michael")
                        .lastName("Doe")
                        .dateOfBirth(LocalDate.of(1995, 5, 20))
                        .gender(Gender.MALE)
                        .maritalStatus(MaritalStatus.SINGLE)
                        .bloodGroup(BloodGroup.O_POSITIVE)
                        .genotype(Genotype.AA)
                        .email("john@test.com")
                        .phoneNumber("08012345678")
                        .build();

        Patient patient = mapper.toEntity(request);

        assertEquals("John", patient.getFirstName());
        assertEquals("Michael", patient.getMiddleName());
        assertEquals("Doe", patient.getLastName());
        assertEquals(LocalDate.of(1995,5,20), patient.getDateOfBirth());
        assertEquals(Gender.MALE, patient.getGender());
        assertEquals(MaritalStatus.SINGLE, patient.getMaritalStatus());
        assertEquals(BloodGroup.O_POSITIVE, patient.getBloodGroup());
        assertEquals(Genotype.AA, patient.getGenotype());
        assertEquals("john@test.com", patient.getEmail());
        assertEquals("08012345678", patient.getPhoneNumber());
    }
    
    @Test
    void shouldDefaultStatusToActive() {

        Patient patient = mapper.toEntity(validRequest());

        assertEquals(
                PatientStatus.ACTIVE,
                patient.getStatus());
    }
    
    @Test
    void shouldDefaultDeceasedToFalse() {

        Patient patient = mapper.toEntity(validRequest());

        assertFalse(patient.getDeceased());
    }
    
    @Test
    void shouldMapEntityToResponse() {

        Patient patient = validPatient();

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                patient.getPatientNumber(),
                response.getPatientNumber());

        assertEquals(
                patient.getGender(),
                response.getGender());

        assertEquals(
                patient.getBloodGroup(),
                response.getBloodGroup());

        assertEquals(
                patient.getStatus(),
                response.getStatus());
    }
    
    @Test
    void shouldBuildFullNameWithMiddleName() {

        Patient patient = validPatient();

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                "John Michael Doe",
                response.getFullName());
    }
    
    @Test
    void shouldBuildFullNameWithoutMiddleName() {

        Patient patient = validPatient();

        patient.setMiddleName(null);

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                "John Doe",
                response.getFullName());
    }
    
    @Test
    void shouldIgnoreBlankMiddleName() {

        Patient patient = validPatient();

        patient.setMiddleName("   ");

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                "John Doe",
                response.getFullName());
    }
    
    @Test
    void shouldCalculateAge() {

        Patient patient = validPatient();

        patient.setDateOfBirth(
                LocalDate.now().minusYears(30));

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                30,
                response.getAge());
    }
    
    @Test
    void shouldMapPhoneAndEmail() {

        Patient patient = validPatient();

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                patient.getPhoneNumber(),
                response.getPhoneNumber());

        assertEquals(
                patient.getEmail(),
                response.getEmail());
    }
    
    @Test
    void shouldMapGenotype() {

        Patient patient = validPatient();

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                Genotype.AA,
                response.getGenotype());
    }
    
    @Test
    void shouldMapBloodGroup() {

        Patient patient = validPatient();

        PatientResponse response =
                mapper.toResponse(patient);

        assertEquals(
                BloodGroup.O_POSITIVE,
                response.getBloodGroup());
    }
    
    @Test
    void shouldMapDeceasedFlag() {

        Patient patient = validPatient();

        patient.setDeceased(true);

        PatientResponse response =
                mapper.toResponse(patient);

        assertTrue(response.getDeceased());
    }
    
    private CreatePatientRequest validRequest() {

        return CreatePatientRequest.builder()
                .firstName("John")
                .middleName("Michael")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995,5,20))
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .genotype(Genotype.AA)
                .email("john@test.com")
                .phoneNumber("08012345678")
                .build();
    }
    
    private Patient validPatient() {

        return Patient.builder()
                .patientNumber("HMS-2026-000001")
                .firstName("John")
                .middleName("Michael")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995,5,20))
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .genotype(Genotype.AA)
                .email("john@test.com")
                .phoneNumber("08012345678")
                .status(PatientStatus.ACTIVE)
                .deceased(false)
                .build();
    }
    
    
}

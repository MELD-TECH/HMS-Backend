package com.hms.patient.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hms.patient.entity.Patient;

public interface PatientRepository
        extends JpaRepository<Patient, UUID>,
                JpaSpecificationExecutor<Patient> {

    Optional<Patient>
    findByPatientNumber(String patientNumber);

    boolean existsByPatientNumber(
            String patientNumber);

    boolean existsByPhoneNumber(
            String phoneNumber);

    boolean existsByEmailIgnoreCase(
            String email);

    @Query(

    		value = """

    		SELECT nextval(

    		'patient_schema.patient_number_seq')

    		""",

    		nativeQuery = true)

    		Long nextPatientSequence();

}
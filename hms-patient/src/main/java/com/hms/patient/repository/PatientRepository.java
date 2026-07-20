package com.hms.patient.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    
    Optional<Patient> findById(UUID id);


    Page<Patient> findAll(
            Specification<Patient> specification,
            Pageable pageable);


    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            UUID id);

    boolean existsByPhoneNumberAndIdNot(
            String phoneNumber,
            UUID id);
    

    Optional<Patient> findByIdAndArchivedTrue(
            UUID patientId);
    
    boolean existsByIdAndArchivedTrue(
            UUID patientId);
    
    @Query("""
    	    SELECT p
    	    FROM Patient p
    	    WHERE
    	        p.id = :patientId
    	    AND
    	        p.archived = true
    	    """)
    	Optional<Patient> findArchivedPatient(
    	        UUID patientId);
}
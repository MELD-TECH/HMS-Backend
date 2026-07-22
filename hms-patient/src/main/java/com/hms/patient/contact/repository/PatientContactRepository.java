package com.hms.patient.contact.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.patient.contact.entity.PatientContact;
import com.hms.patient.contact.enums.ContactStatus;
import com.hms.patient.contact.enums.ContactType;

public interface PatientContactRepository
        extends JpaRepository<PatientContact, UUID> {

    List<PatientContact> findByPatientId(UUID patientId);

    List<PatientContact> findByPatientIdAndStatus(
            UUID patientId,
            ContactStatus status);

    Optional<PatientContact> findByIdAndPatientId(
            UUID id,
            UUID patientId);

    Optional<PatientContact> findByPatientIdAndPrimaryContactTrue(
            UUID patientId);

    boolean existsByPatientIdAndContactTypeAndContactValue(
            UUID patientId,
            ContactType contactType,
            String contactValue);

    boolean existsByPatientIdAndContactTypeAndContactValueAndIdNot(
            UUID patientId,
            ContactType contactType,
            String contactValue,
            UUID id);

    long countByPatientId(UUID patientId);

    long countByPatientIdAndStatus(
            UUID patientId,
            ContactStatus status);

    List<PatientContact> findByPatientIdOrderByPrimaryContactDescCreatedAtAsc(
            UUID patientId);

}
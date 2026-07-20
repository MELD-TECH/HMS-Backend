package com.hms.patient.approval.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.patient.approval.entity.PatientReverseDeceasedRequest;
import com.hms.patient.approval.enums.ApprovalStatus;

public interface PatientReverseDeceasedRequestRepository
extends JpaRepository<
        PatientReverseDeceasedRequest,
        UUID> {

	Optional<PatientReverseDeceasedRequest>

		findByIdAndStatus(

				UUID id,

				ApprovalStatus status);

	boolean existsByPatientIdAndStatus(

			UUID patientId,

			ApprovalStatus status);
}
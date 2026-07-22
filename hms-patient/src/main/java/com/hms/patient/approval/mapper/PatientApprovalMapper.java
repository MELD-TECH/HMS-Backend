package com.hms.patient.approval.mapper;

import org.springframework.stereotype.Component;

import com.hms.patient.approval.dto.request.RequestReverseDeceasedRequest;
import com.hms.patient.approval.dto.response.ReverseDeceasedRequestResponse;
import com.hms.patient.approval.entity.PatientReverseDeceasedRequest;
import com.hms.patient.approval.enums.ApprovalStatus;
import com.hms.patient.entity.Patient;

@Component
public class PatientApprovalMapper {

	public PatientReverseDeceasedRequest toEntity(

	        Patient patient,

	        RequestReverseDeceasedRequest request) {

	    if (patient == null) {
	        return null;
	    }

	    PatientReverseDeceasedRequest entity =
	            new PatientReverseDeceasedRequest();

	    entity.setPatient(
	            patient);

	    entity.setPatientNumber(
	            patient.getPatientNumber());

	    entity.setReason(
	            request.getReason());

	    return entity;
	}
	
	public ReverseDeceasedRequestResponse toResponse(

	        PatientReverseDeceasedRequest entity) {

	    if (entity == null) {
	        return null;
	    }

	    return ReverseDeceasedRequestResponse

	            .builder()

	            .id(entity.getId())

	            .patientId(entity.getId())

	            .patientNumber(entity.getPatientNumber())

	            .reason(entity.getReason())

	            .status(entity.getStatus())

	            .requestedBy(entity.getRequestedBy())

	            .requestedAt(entity.getRequestedAt())

	            .approvedBy(entity.getApprovedBy())

	            .approvedAt(entity.getApprovedAt())

	            .rejectedBy(entity.getRejectedBy())

	            .rejectedAt(entity.getRejectedAt())

	            .rejectionReason(
	                    entity.getRejectionReason())
	            
	            .pending(entity.getStatus() == ApprovalStatus.PENDING)

	            .completed(entity.getStatus() != ApprovalStatus.PENDING)
	            
	            .build();
	}
}

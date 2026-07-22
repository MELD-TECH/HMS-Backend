package com.hms.patient.approval.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.PatientAlreadyPendingRequestException;
import com.hms.common.exception.PatientNotFoundException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.events.security.patient.PatientDeceasedReversalApprovedEvent;
import com.hms.events.security.patient.PatientDeceasedReversalRejectedEvent;
import com.hms.events.security.patient.PatientDeceasedReversalRequestedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.patient.approval.dto.request.ApproveReverseDeceasedRequest;
import com.hms.patient.approval.dto.request.RejectReverseDeceasedRequest;
import com.hms.patient.approval.dto.request.RequestReverseDeceasedRequest;
import com.hms.patient.approval.dto.response.ReverseDeceasedRequestResponse;
import com.hms.patient.approval.entity.PatientReverseDeceasedRequest;
import com.hms.patient.approval.enums.ApprovalStatus;
import com.hms.patient.approval.mapper.PatientApprovalMapper;
import com.hms.patient.approval.repository.PatientReverseDeceasedRequestRepository;
import com.hms.patient.approval.service.PatientApprovalService;
import com.hms.patient.approval.validation.ReverseDeceasedApprovalValidator;
import com.hms.patient.approval.validation.ReverseDeceasedRequestValidator;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.lifecycle.PatientLifecycleManager;
import com.hms.patient.mapper.PatientMapper;
import com.hms.patient.repository.PatientRepository;
import com.hms.security.util.SecurityUtils;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientApprovalServiceImpl
        implements PatientApprovalService {

    private final PatientRepository patientRepository;
    private final PatientReverseDeceasedRequestRepository requestRepository;
    private final PatientLifecycleManager lifecycleManager;
    private final ReverseDeceasedRequestValidator requestValidator;
    private final ReverseDeceasedApprovalValidator approvalValidator;
    private final SecurityEventPublisher publisher;
    private final EntityManager entityManager;
    
    private final PatientMapper patientMapper;
    private final PatientApprovalMapper approvalMapper;

    @Override
    public ReverseDeceasedRequestResponse requestReverseDeceased(
            UUID patientId,
            RequestReverseDeceasedRequest request) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        requestValidator.validate(patient);

        if (requestRepository.existsByPatientIdAndStatus(
                patientId,
                ApprovalStatus.PENDING)) {

            throw new PatientAlreadyPendingRequestException(
                    "A pending reversal request already exists.");
        }
        
        PatientReverseDeceasedRequest entity =
                approvalMapper.toEntity(
                        patient,
                        request);

        entity.setRequestedBy(SecurityUtils.getCurrentUsername());

        entity.setRequestedAt(LocalDateTime.now());

        entity.setStatus(ApprovalStatus.PENDING);

        PatientReverseDeceasedRequest saved =
                requestRepository.save(entity);

        requestRepository.flush();

        entityManager.clear();

        publishRequested(saved);

        return approvalMapper.toResponse(saved);
    }

    
    @Override
    @Transactional
    public PatientResponse approveReverseDeceased(
            UUID requestId,
            ApproveReverseDeceasedRequest request) {

    	PatientReverseDeceasedRequest approval =
    	        requestRepository.findById(requestId)
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException(
    	                        "Reverse deceased approval request not found."));
    	
        approvalValidator.validateApproval(approval);

        Patient patient =
                patientRepository.findById(
                        approval.getPatient().getId())
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                approval.getPatient().getId()));

        lifecycleManager.reverseDeceased(
                patient,
                SecurityUtils.getCurrentUsername(),
                approval.getReason());

        Patient saved =
                patientRepository.save(patient);

        approval.approve(
                SecurityUtils.getCurrentUsername(),
                request.getApprovalComment());

        requestRepository.save(approval);

        patientRepository.flush();

        requestRepository.flush();

        entityManager.clear();

        publishApproved(saved, approval);

        return patientMapper.toResponse(saved);
    }
    
    @Override
    @Transactional
    public void rejectReverseDeceased(
            UUID requestId,
            RejectReverseDeceasedRequest request) {

    	PatientReverseDeceasedRequest approval =
    	        requestRepository.findById(requestId)
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException(
    	                        "Reverse deceased approval request not found."));

        approvalValidator.validateApproval(approval);

        approval.reject(
                SecurityUtils.getCurrentUsername(),
                request.getRejectionReason());

        requestRepository.save(approval);

        requestRepository.flush();

        entityManager.clear();

        publishRejected(approval);
    }
    
    private void publishRequested(

            PatientReverseDeceasedRequest request) {

        publisher.publish(

                new PatientDeceasedReversalRequestedEvent(

                        request.getRequestedBy(),

                        request.getPatient().getId().toString(),

                        request.getPatientNumber(),

                        request.getReason()));
    }
    
    private void publishApproved(

            Patient patient,

            PatientReverseDeceasedRequest request) {

        publisher.publish(

                new PatientDeceasedReversalApprovedEvent(

                        SecurityUtils.getCurrentUsername(),

                        patient.getId().toString(),

                        patient.getPatientNumber(),

                        request.getRequestedBy()));
    }
    
    private void publishRejected(

            PatientReverseDeceasedRequest request) {

        publisher.publish(

                new PatientDeceasedReversalRejectedEvent(

                        SecurityUtils.getCurrentUsername(),

                        request.getPatient().getId().toString(),

                        request.getPatientNumber(),

                        request.getRequestedBy(),

                        request.getRejectionReason()));
    }
    
}

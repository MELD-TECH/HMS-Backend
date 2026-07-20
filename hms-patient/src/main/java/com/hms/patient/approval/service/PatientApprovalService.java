package com.hms.patient.approval.service;

import java.util.UUID;

import com.hms.patient.approval.dto.request.ApproveReverseDeceasedRequest;
import com.hms.patient.approval.dto.request.RejectReverseDeceasedRequest;
import com.hms.patient.approval.dto.request.RequestReverseDeceasedRequest;
import com.hms.patient.approval.dto.response.ReverseDeceasedRequestResponse;
import com.hms.patient.dto.response.PatientResponse;

public interface PatientApprovalService {

    ReverseDeceasedRequestResponse requestReverseDeceased(
            UUID patientId,
            RequestReverseDeceasedRequest request);

    PatientResponse
        approveReverseDeceased(
            UUID requestId,
            ApproveReverseDeceasedRequest request);

    void rejectReverseDeceased(
            UUID requestId,
            RejectReverseDeceasedRequest request);
}

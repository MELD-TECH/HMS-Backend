package com.hms.patient.approval.validation;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.PatientAlreadyPendingRequestException;
import com.hms.common.exception.PatientNotFoundException;
import com.hms.patient.approval.entity.PatientReverseDeceasedRequest;
import com.hms.patient.approval.enums.ApprovalStatus;
import com.hms.security.util.SecurityUtils;

@Component
public class ReverseDeceasedApprovalValidator {

    public void validateApproval(
            PatientReverseDeceasedRequest request) {
    	
        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new PatientAlreadyPendingRequestException(
            		request.getPatientNumber()
                    );
        }

        if (request.getRequestedBy()
                .equals(SecurityUtils.getCurrentUsername())) {

            throw new BusinessException(
                    "Maker cannot approve own request.");
        }
    }
}
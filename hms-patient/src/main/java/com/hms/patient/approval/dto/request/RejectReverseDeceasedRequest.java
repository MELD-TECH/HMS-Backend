package com.hms.patient.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RejectReverseDeceasedRequest {

    @NotBlank
    private String rejectionReason;
}

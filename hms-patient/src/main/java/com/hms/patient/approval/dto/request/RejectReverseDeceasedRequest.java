package com.hms.patient.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RejectReverseDeceasedRequest {

    @NotBlank
    private String rejectionReason;
}

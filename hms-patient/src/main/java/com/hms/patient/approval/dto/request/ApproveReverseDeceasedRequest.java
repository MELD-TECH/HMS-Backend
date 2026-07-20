package com.hms.patient.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ApproveReverseDeceasedRequest {

    @NotBlank
    private String approvalComment;
}

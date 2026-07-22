package com.hms.patient.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApproveReverseDeceasedRequest {

    @NotBlank
    @Size(min = 5, max = 500)
    private String approvalComment;
}

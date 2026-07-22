package com.hms.patient.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestReverseDeceasedRequest {

    @NotBlank
    @Size(min = 10, max = 500)
    private String reason;
}

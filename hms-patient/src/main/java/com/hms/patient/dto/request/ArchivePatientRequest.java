package com.hms.patient.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivePatientRequest {

    @NotBlank(message = "Archive reason is required.")
    @Size(
            min = 5,
            max = 500,
            message = "Archive reason must be between 5 and 500 characters.")
    private String reason;
}
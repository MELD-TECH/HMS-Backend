package com.hms.patient.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
public class DeceasedPatientRequest {

    @NotNull(message = "Date of death is required.")
    @PastOrPresent(message = "Date of death cannot be in the future.")
    private LocalDate deceasedDate;

    @NotBlank(message = "Cause of death is required.")
    @Size(
            min = 5,
            max = 500,
            message = "Cause of death must be between 5 and 500 characters.")
    private String causeOfDeath;

    @NotBlank(message = "Reason is required.")
    @Size(
            min = 5,
            max = 1000,
            message = "Reason must be between 5 and 500 characters.")
    private String deceasedNotes;
}
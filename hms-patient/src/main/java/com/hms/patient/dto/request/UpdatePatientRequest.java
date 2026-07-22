package com.hms.patient.dto.request;

import java.time.LocalDate;

import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
public class UpdatePatientRequest {

    @NotBlank
    @Size(max = 80)
    private String firstName;

    @Size(max = 80)
    private String middleName;

    @NotBlank
    @Size(max = 80)
    private String lastName;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotNull
    private Gender gender;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    private BloodGroup bloodGroup;

    @NotNull
    private Genotype genotype;

    @Email
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(\\+234|0)[789][01][0-9]{8}$",
        message = "Invalid phone number")
    private String phoneNumber;
    
    @NotNull
    private Long version;
}
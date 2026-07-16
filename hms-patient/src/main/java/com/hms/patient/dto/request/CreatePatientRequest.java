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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePatientRequest {

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
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

    @Pattern(
            regexp="^[0-9+]{10,20}$")
    private String phoneNumber;

}

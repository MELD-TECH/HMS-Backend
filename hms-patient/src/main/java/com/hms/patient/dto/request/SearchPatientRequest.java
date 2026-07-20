package com.hms.patient.dto.request;

import java.time.LocalDate;

import com.hms.patient.enums.Gender;
import com.hms.patient.enums.PatientStatus;

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
public class SearchPatientRequest {

    private String patientNumber;

    private String firstName;

    private String middleName;

    private String lastName;

    /**
     * General search text.
     * Searches patient number, first name,
     * middle name, last name,
     * phone number and email.
     */
    private String keyword;

    private String phoneNumber;

    private String email;

    private Gender gender;

    private PatientStatus status;

    private LocalDate dateOfBirth;
}
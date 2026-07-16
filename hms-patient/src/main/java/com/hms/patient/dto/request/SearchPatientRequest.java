package com.hms.patient.dto.request;

import com.hms.patient.enums.Gender;
import com.hms.patient.enums.PatientStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPatientRequest {

    private String patientNumber;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private Gender gender;

    private PatientStatus status;

}
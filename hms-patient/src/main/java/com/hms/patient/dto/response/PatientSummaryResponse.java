package com.hms.patient.dto.response;

import java.util.UUID;

import com.hms.patient.enums.Gender;
import com.hms.patient.enums.PatientStatus;

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
public class PatientSummaryResponse {

    private UUID id;

    private String patientNumber;

    private String fullName;

    private Integer age;

    private Gender gender;

    private String phoneNumber;

    private PatientStatus status;

}
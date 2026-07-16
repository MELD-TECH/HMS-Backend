package com.hms.patient.mapper;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class PatientMapper {

    public Patient toEntity(
            CreatePatientRequest request) {

        return Patient.builder()

                .firstName(request.getFirstName())

                .middleName(request.getMiddleName())

                .lastName(request.getLastName())

                .dateOfBirth(request.getDateOfBirth())

                .gender(request.getGender())

                .maritalStatus(request.getMaritalStatus())

                .bloodGroup(request.getBloodGroup())

                .genotype(request.getGenotype())

                .email(request.getEmail())

                .phoneNumber(request.getPhoneNumber())

                .status(PatientStatus.ACTIVE)

                .deceased(false)

                .build();
    }
    
    public PatientResponse toResponse(

            Patient patient) {

        return PatientResponse.builder()

                .id(patient.getId())

                .patientNumber(

                        patient.getPatientNumber())

                .firstName(

                        patient.getFirstName())

                .middleName(

                        patient.getMiddleName())

                .lastName(

                        patient.getLastName())

                .fullName(

                        buildFullName(patient))

                .age(

                        calculateAge(patient))

                .gender(

                        patient.getGender())

                .bloodGroup(

                        patient.getBloodGroup())

                .genotype(

                        patient.getGenotype())

                .phoneNumber(

                        patient.getPhoneNumber())

                .email(

                        patient.getEmail())

                .status(

                        patient.getStatus())

                .deceased(

                        patient.getDeceased())

                .build();

    }
    
    private Integer calculateAge(

            Patient patient) {

        return Period

                .between(

                        patient.getDateOfBirth(),

                        LocalDate.now())

                .getYears();

    }
    
    private String buildFullName(

            Patient patient) {

        return Stream.of(

                patient.getFirstName(),

                patient.getMiddleName(),

                patient.getLastName())

                .filter(

                        Objects::nonNull)

                .filter(

                        s -> !s.isBlank())

                .collect(

                        Collectors.joining(" "));

    }
}

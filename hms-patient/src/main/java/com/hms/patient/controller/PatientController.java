package com.hms.patient.controller;

import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;
import com.hms.patient.service.PatientService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT_CREATE')")
    public ResponseEntity<PatientResponse> register(
            @Valid
            @RequestBody CreatePatientRequest request) {

        PatientResponse response =
                patientService.register(request);

        URI location = URI.create(
                "/api/v1/patients/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

}
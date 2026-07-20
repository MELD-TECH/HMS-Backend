package com.hms.patient.service;

import java.util.UUID;

import com.hms.patient.dto.request.ActivatePatientRequest;
import com.hms.patient.dto.request.ArchivePatientRequest;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.dto.request.RestorePatientRequest;
import com.hms.patient.dto.request.UpdatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;

public interface PatientService {

    PatientResponse register(

            CreatePatientRequest request);
    
    PatientResponse update(
            UUID id,
            UpdatePatientRequest request);
    
    PatientResponse archive(
            UUID id,
            ArchivePatientRequest request);
    
    PatientResponse activate(

            UUID id,

            ActivatePatientRequest request);
   
    PatientResponse markDeceased(
            UUID id,
            DeceasedPatientRequest request);
   
    PatientResponse restore(
            UUID patientId,
            RestorePatientRequest request);
    

}

package com.hms.patient.service;

import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.response.PatientResponse;

public interface PatientService {

    PatientResponse register(

            CreatePatientRequest request);

}

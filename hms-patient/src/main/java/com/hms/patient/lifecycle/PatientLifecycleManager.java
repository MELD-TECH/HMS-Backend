package com.hms.patient.lifecycle;

import java.time.LocalDate;

import com.hms.patient.dto.request.ArchivePatientRequest;
import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.entity.Patient;

public interface PatientLifecycleManager {

    void archive(
    	    Patient patient,
    	    String reason,
    	    String username);

    void activate(
            Patient patient);

    void markDeceased(
    	    Patient patient,
    	    LocalDate deceasedDate,
    	    String causeOfDeath,
    	    String notes);

    void restore(Patient patient);
    
    void reverseDeceased(

            Patient patient,

            String username,

            String reason);
}
package com.hms.patient.lifecycle;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.PatientStatus;

@Component
public class PatientLifecycleManagerImpl
        implements PatientLifecycleManager {

    @Override
    public void archive(
    	    Patient patient,
    	    String reason,
    	    String username){

        patient.setStatus(PatientStatus.ARCHIVED);

        patient.setArchiveReason(reason);

        patient.setArchivedAt(LocalDateTime.now());

        patient.setArchivedBy(username);
    }

    @Override
    public void activate(
            Patient patient) {

        patient.setStatus(PatientStatus.ACTIVE);

        patient.setArchiveReason(null);

        patient.setArchivedAt(null);

        patient.setArchivedBy(null);
    }

    @Override
    public void markDeceased(
    	    Patient patient,
    	    LocalDate deceasedDate,
    	    String causeOfDeath,
    	    String notes) {

        patient.setStatus(PatientStatus.DECEASED);

        patient.setDeceased(true);

        patient.setDeceasedDate(
        		deceasedDate);

        patient.setCauseOfDeath(
        		causeOfDeath);

        patient.setDeceasedNotes(
        		notes);
    }

    @Override
    public void restore(
            Patient patient) {

        patient.setStatus(
                PatientStatus.ACTIVE);

        patient.setArchiveReason(null);

        patient.setArchivedAt(null);

        patient.setArchivedBy(null);
    }
    
    @Override
    public void reverseDeceased(

            Patient patient,

            String username,

            String reason) {

        patient.reverseDeceased(

                username,

                reason);
    }
}
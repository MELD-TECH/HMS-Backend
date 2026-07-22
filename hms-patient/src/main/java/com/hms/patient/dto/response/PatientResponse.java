package com.hms.patient.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
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
public class PatientResponse {

    private UUID id;

    private String patientNumber;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private LocalDate dateOfBirth;

    private Integer age;

    private Gender gender;

    private MaritalStatus maritalStatus;

    private BloodGroup bloodGroup;

    private Genotype genotype;

    private String email;

    private String phoneNumber;

    private PatientStatus status;

    private Boolean deceased;
    
    private String archiveReason;

    private LocalDateTime archivedAt;

    private String archivedBy;
    
    private LocalDate deceasedDate;
    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
    
    private String causeOfDeath;
  
    private String deceasedNotes;
    
    private Boolean archived; 
    
    private Long version;

}

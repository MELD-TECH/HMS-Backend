package com.hms.patient.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hms.common.BaseEntity;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
import com.hms.patient.enums.PatientStatus;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "patients",
        schema = "patient_schema",
        indexes = {

            @Index(
                    name = "idx_patient_number",
                    columnList = "patientNumber",
                    unique = true
            ),

            @Index(
                    name = "idx_patient_phone",
                    columnList = "phoneNumber"
            ),

            @Index(
                    name = "idx_patient_email",
                    columnList = "email"
            ),

            @Index(
                    name = "idx_patient_name",
                    columnList = "lastName, firstName"
            )
        }
)

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String patientNumber;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(length = 80)
    private String middleName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genotype genotype;

    @Column(length = 120)
    private String email;

    @Column(length = 30)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean deceased = false;

    private LocalDate deceasedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientStatus status;

    /*
     * Future relationship.
     */
    private UUID profilePhotoId;
    
    @Column(length = 500)
    private String archiveReason;

    private LocalDateTime archivedAt;

    @Column(length = 100)
    private String archivedBy;
    
    @Column(name = "archived")
    @Builder.Default
    private Boolean archived = false;
    
    @Column(length = 200)
    private String causeOfDeath;

    @Column(length = 1000)
    private String deceasedNotes;
   
    @Column(length = 500)
    private String deceasedReversalReason;

    private LocalDateTime deceasedReversedAt;

    @Column(length = 100)
    private String deceasedReversedBy;
    
    @Transient
    public String getFullName() {

        return Stream.of(
                firstName,
                middleName,
                lastName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }
    
    public void restore() {

        if (!Boolean.TRUE.equals(archived)) {
            throw new IllegalStateException(
                    "Patient is not archived.");
        }

        if (status == PatientStatus.DECEASED) {
            throw new IllegalStateException(
                    "Deceased patients cannot be restored.");
        }

        archived = false;

        archivedAt = null;

        archivedBy = null;

        archiveReason = null;

        status = PatientStatus.ACTIVE;
    }
    
    public void reverseDeceased(

            String username,

            String reason) {

        if (!Boolean.TRUE.equals(deceased)) {

            throw new IllegalStateException(

                    "Patient is not deceased.");
        }

        deceased = false;

        status = PatientStatus.ACTIVE;

        deceasedReversedBy = username;

        deceasedReversedAt = LocalDateTime.now();

        deceasedReversalReason = reason;
    }
}
package com.hms.patient.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.hms.common.BaseEntity;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
import com.hms.patient.enums.PatientStatus;

import jakarta.persistence.*;

import lombok.*;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
//    @Column(length = 250)
//    private String fullName;

}
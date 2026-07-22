package com.hms.patient.contact.entity;

import java.time.LocalDateTime;

import com.hms.common.BaseEntity;
import com.hms.patient.contact.enums.ContactStatus;
import com.hms.patient.contact.enums.ContactType;
import com.hms.patient.entity.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "patient_contacts",
        schema = "patient_schema")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientContact extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "patient_id",
            nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "contact_type",
            nullable = false,
            length = 30)
    private ContactType contactType;

    @Column(
            name = "contact_value",
            nullable = false,
            length = 150)
    private String contactValue;

    @Column(
            name = "primary_contact",
            nullable = false)
    private Boolean primaryContact;

    @Column(
            nullable = false)
    private Boolean verified;

    private LocalDateTime verifiedAt;

    @Column(length = 100)
    private String verifiedBy;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20)
    private ContactStatus status;

}
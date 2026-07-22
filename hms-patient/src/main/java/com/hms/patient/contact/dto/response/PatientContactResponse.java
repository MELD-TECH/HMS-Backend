package com.hms.patient.contact.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.patient.contact.enums.ContactStatus;
import com.hms.patient.contact.enums.ContactType;

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
public class PatientContactResponse {

    private UUID id;

    private UUID patientId;

    private ContactType contactType;

    private String contactValue;

    private Boolean primaryContact;

    private Boolean verified;

    private LocalDateTime verifiedAt;

    private String verifiedBy;

    private ContactStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}

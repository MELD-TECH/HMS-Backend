package com.hms.patient.contact.dto.request;

import com.hms.patient.contact.enums.ContactType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdatePatientContactRequest {

    @NotNull
    private ContactType contactType;

    @NotBlank
    @Size(max = 150)
    private String contactValue;

    @NotNull
    private Boolean primaryContact;

    @NotNull
    private Long version;
}

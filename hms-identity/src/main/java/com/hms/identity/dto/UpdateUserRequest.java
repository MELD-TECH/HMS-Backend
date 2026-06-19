package com.hms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @Email
        String email

) {
}
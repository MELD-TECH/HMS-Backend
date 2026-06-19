package com.hms.identity.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(

        @NotBlank
        String username,

        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName

) {
}
package com.hms.identity.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteAuthenticationRequest(

        @NotBlank
        String challengeToken,

        @NotBlank
        String otp

) {
}
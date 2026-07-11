package com.hms.notification.dto;

import java.util.UUID;

import com.hms.notification.mfa.enums.MfaType;

import jakarta.validation.constraints.NotNull;

public record ResendOtpRequest(

        @NotNull
        UUID userId,

        @NotNull
        MfaType type,

        @NotNull
        String recipient
) {
}
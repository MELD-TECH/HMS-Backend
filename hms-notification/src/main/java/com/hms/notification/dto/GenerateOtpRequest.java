package com.hms.notification.dto;

import java.util.UUID;

import com.hms.notification.mfa.enums.MfaType;

import lombok.Builder;

@Builder
public record GenerateOtpRequest(

        UUID userId,

        String recipient,

        MfaType type,

        String initiatedBy

) {
}

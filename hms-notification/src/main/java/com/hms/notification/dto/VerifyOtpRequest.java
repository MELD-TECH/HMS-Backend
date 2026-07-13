package com.hms.notification.dto;

import java.util.UUID;

import com.hms.notification.mfa.enums.MfaType;

import lombok.Builder;

@Builder
public record VerifyOtpRequest(

        UUID userId,

        String code,

        MfaType type,

        String initiatedBy

) {
}
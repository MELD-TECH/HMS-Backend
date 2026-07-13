package com.hms.notification.dto;

import java.util.UUID;

import com.hms.notification.mfa.enums.MfaType;
import lombok.Builder;

@Builder
public record ResendOtpRequest(

        UUID userId,

        MfaType type,

        String recipient,

        String initiatedBy

) {
}
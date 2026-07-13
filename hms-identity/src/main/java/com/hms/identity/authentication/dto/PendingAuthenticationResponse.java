package com.hms.identity.authentication.dto;

import java.time.LocalDateTime;

import com.hms.notification.mfa.enums.MfaType;

import lombok.Builder;

@Builder
public record PendingAuthenticationResponse(

        String challengeToken,

        LocalDateTime expiresAt,

        MfaType mfaType) {
}

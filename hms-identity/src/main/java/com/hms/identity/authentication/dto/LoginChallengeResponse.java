package com.hms.identity.authentication.dto;

import java.time.LocalDateTime;

import com.hms.notification.mfa.enums.MfaType;

import lombok.Builder;

@Builder
public record LoginChallengeResponse(

        boolean mfaRequired,

        String challengeToken,

        MfaType mfaType,

        LocalDateTime expiresAt) {
}
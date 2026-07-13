package com.hms.identity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.notification.mfa.enums.MfaType;

import lombok.Builder;

@Builder
public record LoginResponse(boolean mfaRequired, String accessToken, String refreshToken, UUID sessionId, String tokenType,
        LocalDateTime expiresAt, long expiresInSeconds, String challengeToken, MfaType mfaType) {
	
}
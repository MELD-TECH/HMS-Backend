package com.hms.identity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoginResponse(String accessToken, String refreshToken, UUID sessionId, String tokenType,
        LocalDateTime expiresAt, long expiresInSeconds) {
	
}
package com.hms.api.dto;

import java.time.LocalDateTime;

public record LoginResponse(String accessToken, String tokenType,
        LocalDateTime expiresAt, long expiresInSeconds) {
	
}
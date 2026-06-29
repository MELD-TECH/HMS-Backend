package com.hms.identity.session.dto;

import java.time.LocalDateTime;

public record RefreshTokenResponse(

        String accessToken,

        String refreshToken,

        String tokenType,

        LocalDateTime expiresAt,

        Long expiresInSeconds

) {}

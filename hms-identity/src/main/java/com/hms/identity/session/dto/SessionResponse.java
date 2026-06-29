package com.hms.identity.session.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionResponse(

        UUID id,

        String deviceName,

        String ipAddress,

        String userAgent,

        LocalDateTime createdAt,

        LocalDateTime lastUsedAt,

        boolean revoked

) {}

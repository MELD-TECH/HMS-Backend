package com.hms.identity.audit.dto;

import lombok.Builder;

@Builder
public record AuditRequest(

        String username,

        String action,

        String module,

        String entity,

        String entityId,

        String beforeJson,

        String afterJson,

        String details,

        String ipAddress,

        String userAgent

) {}
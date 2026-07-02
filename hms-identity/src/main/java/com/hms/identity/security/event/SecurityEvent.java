package com.hms.identity.security.event;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public abstract class SecurityEvent {

    private final String username;

    private final String entity;

    private final String entityId;

    private final LocalDateTime occurredAt;

    protected SecurityEvent(
            String username,
            String entity,
            String entityId) {

        this.username = username;
        this.entity = entity;
        this.entityId = entityId;
        this.occurredAt = LocalDateTime.now();
    }

    public abstract String action();

    public abstract String details();
}
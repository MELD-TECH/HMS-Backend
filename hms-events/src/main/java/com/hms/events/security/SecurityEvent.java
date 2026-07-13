package com.hms.events.security;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public abstract class SecurityEvent extends ApplicationEvent {

    private final String username;

    private final String entity;

    private final String entityId;

    protected SecurityEvent(
            String username,
            String entity,
            String entityId) {

        // Spring requires exactly ONE source object
        super(username);

        this.username = username;
        this.entity = entity;
        this.entityId = entityId;
    }

    public abstract String action();

    public abstract String details();
}
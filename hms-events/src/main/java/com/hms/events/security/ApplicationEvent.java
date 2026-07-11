package com.hms.events.security;

public abstract class ApplicationEvent {

    private final String username;

    private final String entity;

    private final String entityId;

    protected ApplicationEvent(
            String username,
            String entity,
            String entityId) {

        this.username = username;
        this.entity = entity;
        this.entityId = entityId;

    }

    public String getUsername() {
        return username;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public abstract String action();

    public abstract String details();

}
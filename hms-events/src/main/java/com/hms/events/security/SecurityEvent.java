package com.hms.events.security;


import lombok.Getter;

@Getter
public abstract class SecurityEvent
extends ApplicationEvent {

protected SecurityEvent(
    String username,
    String entity,
    String entityId) {

super(
        username,
        entity,
        entityId);

}

}
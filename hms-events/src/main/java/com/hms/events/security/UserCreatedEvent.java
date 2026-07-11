package com.hms.events.security;

public class UserCreatedEvent
extends SecurityEvent {

public UserCreatedEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "USER_CREATED";
}

@Override
public String details() {

return "New user created";
}
}
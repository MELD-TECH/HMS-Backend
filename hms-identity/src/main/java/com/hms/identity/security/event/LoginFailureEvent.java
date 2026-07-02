package com.hms.identity.security.event;

public class LoginFailureEvent
extends SecurityEvent {

public LoginFailureEvent(
    String username,
    String entityId) {

super(username, "USER", entityId);
}

@Override
public String action() {
return "LOGIN_FAILED";
}

@Override
public String details() {
return "Invalid username or password";
}
}
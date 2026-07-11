package com.hms.events.security;

public class LoginSuccessEvent
extends SecurityEvent {

public LoginSuccessEvent(
    String username,
    String entityId) {

super(username, "USER", entityId);
}

@Override
public String action() {
return "LOGIN_SUCCESS";
}

@Override
public String details() {
return "User authenticated successfully";
}
}

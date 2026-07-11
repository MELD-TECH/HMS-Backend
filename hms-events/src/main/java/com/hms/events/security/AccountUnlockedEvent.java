package com.hms.events.security;

public class AccountUnlockedEvent
extends SecurityEvent {

public AccountUnlockedEvent(
    String username,
    String entityId) {

super(username, "USER", entityId);
}

@Override
public String action() {
return "ACCOUNT_UNLOCKED";
}

@Override
public String details() {
return "Administrator unlocked account";
}
}

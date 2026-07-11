package com.hms.events.security;

public class OtpGeneratedEvent  extends SecurityEvent {

	public OtpGeneratedEvent(String username, String entityId) {
		super(username, "USER", entityId);
	}

	@Override
	public String action() {
		return "OTP_GENERATED";
	}

	@Override
	public String details() {
		return "OTP generated successfully";
	}
}

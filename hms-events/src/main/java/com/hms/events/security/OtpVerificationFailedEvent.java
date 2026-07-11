package com.hms.events.security;

public class OtpVerificationFailedEvent extends SecurityEvent {

	public OtpVerificationFailedEvent(String username, String entityId) {
		super(username, "USER", entityId);
	}

	@Override
	public String action() {
		return "OTP_VERIFICATION_FAILED";
	}

	@Override
	public String details() {
		return "OTP verification failed";
	}

}

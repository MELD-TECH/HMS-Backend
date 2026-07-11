package com.hms.events.security;

public class OtpRetryLimitExceededEvent extends SecurityEvent {

	public OtpRetryLimitExceededEvent(String username, String entityId) {
		super(username, "USER", entityId);
	}

	@Override
	public String action() {
		return "OTP_RETRY_LIMIT_EXCEEDED";
	}

	@Override
	public String details() {
		return "OTP retry limit exceeded";
	}

}

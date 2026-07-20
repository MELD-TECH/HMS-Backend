package com.hms.common.exception;

public class DuplicatePatientPhoneException 
extends BusinessException {

	public DuplicatePatientPhoneException(String phoneNumber) {
		super("Duplicate phone number: '%s' ".formatted(phoneNumber));
	}
}

package com.hms.common.exception;

public class DuplicatePatientPhoneException 
extends BusinessException {

	public DuplicatePatientPhoneException() {
		super("Duplicate phone number record");
	}
}

package com.hms.common.exception;

public class InvalidPatientDateOfBirthException 
extends BusinessException {

	public InvalidPatientDateOfBirthException() {
		super("Invalid date of birth record");
	}
}

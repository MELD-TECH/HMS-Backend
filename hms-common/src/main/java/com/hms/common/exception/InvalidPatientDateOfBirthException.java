package com.hms.common.exception;

import java.time.LocalDate;

public class InvalidPatientDateOfBirthException 
extends BusinessException {

	public InvalidPatientDateOfBirthException(LocalDate dateOfBirth) {
		super("Invalid date of birth: ['%s']. ".formatted(dateOfBirth));
	}
}

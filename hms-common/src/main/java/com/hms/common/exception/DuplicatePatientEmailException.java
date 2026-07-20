package com.hms.common.exception;

public class DuplicatePatientEmailException
extends BusinessException {

	public DuplicatePatientEmailException(String email) {
		super("Duplicate patient email: '%s".formatted(email));
	}
}

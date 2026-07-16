package com.hms.common.exception;

public class DuplicatePatientEmailException
extends BusinessException {

	public DuplicatePatientEmailException() {
		super("Duplicate patient email record");
	}
}

package com.hms.common.exception;

public class OptimisticLockBusinessException extends BusinessException  {

	public OptimisticLockBusinessException() {
		super("Patient record has been modified by another user.");
	}
}

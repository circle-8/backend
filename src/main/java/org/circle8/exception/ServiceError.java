package org.circle8.exception;

import org.circle8.controller.response.ErrorCode;

public class ServiceError extends ServiceException {
	public ServiceError(String message, String devMessage) { super(message, devMessage); }

	public ServiceError(String message, Throwable cause) { super(message, cause); }

	@Override public ErrorCode code() { return ErrorCode.INTERNAL_ERROR; }
}

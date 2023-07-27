package org.circle8.exception;

import org.circle8.controller.response.ErrorCode;

public class NotFoundException extends ServiceException {
	public NotFoundException(String message) { super(message, ""); }

	@Override public ErrorCode code() { return ErrorCode.NOT_FOUND; }
}

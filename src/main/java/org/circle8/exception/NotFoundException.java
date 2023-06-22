package org.circle8.exception;

public class NotFoundException extends ServiceException {
	public NotFoundException(String message) { super(message, ""); }
}

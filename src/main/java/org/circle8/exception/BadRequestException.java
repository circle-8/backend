package org.circle8.exception;

public class BadRequestException extends ServiceException{

	public BadRequestException(String message) {
		super(message);
	}
	public BadRequestException(String message, Throwable cause) { super(message, cause); }
}

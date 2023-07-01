package org.circle8.exception;

public class ServiceError extends ServiceException {
	public ServiceError(String message, String devMessage) { super(message, devMessage); }

	public ServiceError(String message, Throwable cause) { super(message, cause); }
}

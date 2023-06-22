package org.circle8.exception;

import lombok.Getter;

public class ServiceException extends Exception {
	@Getter
	private final String devMessage;

	public ServiceException(String message) {
		super(message);
		this.devMessage = "";
	}

	public ServiceException(String message, String devMessage) {
		super(message);
		this.devMessage = devMessage;
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		this.devMessage = cause.getMessage();
	}
}

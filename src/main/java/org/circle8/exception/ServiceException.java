package org.circle8.exception;

import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.IErrorResponse;

import lombok.Getter;

public class ServiceException extends Exception implements IErrorResponse {
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

	@Override public ErrorCode code() { return ErrorCode.BAD_REQUEST; }
	@Override public String message() { return this.getMessage(); }
	@Override public String dev() { return this.devMessage; }
}

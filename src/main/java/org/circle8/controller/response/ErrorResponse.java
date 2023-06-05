package org.circle8.response;

import io.javalin.http.HttpStatus;

public class ErrorResponse implements ApiResponse {
	public ErrorCode code;
	public String message;
	public String devMessage;

	@Override
	public HttpStatus status() { return code.toStatus(); }
}

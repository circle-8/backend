package org.circle8.controller.response;

import io.javalin.http.HttpStatus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse implements ApiResponse {
	public ErrorCode code;
	public String message;
	public String devMessage;

	@Override
	public HttpStatus status() { return code.toStatus(); }
}

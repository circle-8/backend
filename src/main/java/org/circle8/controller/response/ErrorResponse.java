package org.circle8.controller.response;

import io.javalin.http.HttpStatus;

import org.circle8.controller.request.IRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse implements ApiResponse {
	public ErrorCode code;
	public String message;
	public String devMessage;

	public ErrorResponse(IRequest.Validation v) { this(ErrorCode.BAD_REQUEST, v.message(), ""); }

	public ErrorResponse(IErrorResponse e) { this(e.code(), e.message(), e.dev()); }

	@Override
	public HttpStatus status() { return code.toStatus(); }
}

package org.circle8.controller.response;

import io.javalin.http.HttpStatus;
import lombok.AllArgsConstructor;
import org.circle8.controller.request.IRequest;

@AllArgsConstructor
public class ErrorResponse implements ApiResponse {
	public ErrorCode code;
	public String message;
	public String devMessage;

	public ErrorResponse(IRequest.Validation v) {
		this(ErrorCode.BAD_REQUEST, v.message(), "");
	}

	@Override
	public HttpStatus status() { return code.toStatus(); }
}

package org.circle8.controller.response;

import io.javalin.http.HttpStatus;

public enum ErrorCode {
	INTERNAL_ERROR,
	BAD_REQUEST,
	NOT_FOUND,
	TOKEN_ERROR,
	TOKEN_NOT_FOUND,
	TOKEN_EXPIRED
	;

	public HttpStatus toStatus() {
		return switch(this) {
			case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
			case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
			case NOT_FOUND -> HttpStatus.NOT_FOUND;
			case TOKEN_ERROR, TOKEN_NOT_FOUND, TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
		};
	}
}

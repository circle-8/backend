package org.circle8.response;

import io.javalin.http.HttpStatus;

public interface ApiResponse {
	default HttpStatus status() {
		return HttpStatus.OK;
	}
}

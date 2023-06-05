package org.circle8.controller.response;

import io.javalin.http.HttpStatus;

public interface ApiResponse {
	default HttpStatus status() {
		return HttpStatus.OK;
	}
}

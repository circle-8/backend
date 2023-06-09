package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.SuscripcionResponse;

import java.time.LocalDateTime;
import java.util.List;

public class SuscripcionController {
	private final SuscripcionResponse mock = SuscripcionResponse.builder()
		.id(1)
		.ultimaRenovacion(LocalDateTime.now().minusDays(120))
		.proximaRenovacion(LocalDateTime.now().plusDays(120))
		.build();
	/**
	 * GET /suscripciones
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
	/**
	 * GET /user/{id_user}/suscripcion
	 */
	public ApiResponse get(Context ctx) {
		return mock;
	}

	/**
	 * POST /user/{id_user}/suscripcion
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * DELETE /user/{id_user}/suscripcion
	 */
	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}
}

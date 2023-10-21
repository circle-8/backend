package org.circle8.controller;

import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PlanResponse;

import java.math.BigDecimal;
import java.util.List;

@Singleton
public class PlanController {
	private final PlanResponse mock = PlanResponse.builder()
		.id(1)
		.nombre("plan prueba")
		.precio(BigDecimal.TEN)
		.mesesRenovacion(3)
		.cantidadUsuarios(1)
		.build();

	/**
	 * GET /plan/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * PUT /plan/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /plan
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}

	/**
	 * GET /planes
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

package org.circle8.controller;

import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoVerdeResponse;
import org.circle8.controller.response.TipoResiduoResponse;

import java.util.List;

@Singleton
public class PuntoVerdeController {
	private final PuntoVerdeResponse mock = PuntoVerdeResponse.builder()
		.id(1)
		.latitud(-34.6516556f).longitud(-58.5356009f)
		.dias(List.of(DiaResponse.LUNES, DiaResponse.MIERCOLES, DiaResponse.SABADO))
		.tipoResiduo(List.of(
			new TipoResiduoResponse(1, "ORGANICO"),
			new TipoResiduoResponse(2, "PLASTICO")
		))
		.build();
	/**
	 * GET /punto_verde/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * PUT /punto_verde/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /punto_verde
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * DELETE /punto_verde/{id}
	 */
	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}

	/**
	 * GET /puntos_verdes
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

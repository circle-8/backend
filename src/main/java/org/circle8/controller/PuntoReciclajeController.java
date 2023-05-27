package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.DiaResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.PuntoReciclajeResponse;
import org.circle8.response.TipoResiduoResponse;

import java.util.List;

public class PuntoReciclajeController {
	private final PuntoReciclajeResponse mock = PuntoReciclajeResponse.builder()
		.id(1)
		.latitud(-34.6701907f).longitud(-58.5656422f)
		.dias(List.of(DiaResponse.LUNES, DiaResponse.MIERCOLES))
		.tipoResiduo(List.of(new TipoResiduoResponse(1, "ORGANICO"), new TipoResiduoResponse(2, "PLASTICO")))
		.build();

	/**
	 * GET /reciclador/{id_reciclador}/punto_reciclaje/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Integer.parseInt(ctx.pathParam("id_reciclador")))
			.build();
	}
	/**
	 * PUT /reciclador/{id_reciclador}/punto_reciclaje/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Integer.parseInt(ctx.pathParam("id_reciclador")))
			.build();
	}

	/**
	 * DELETE /reciclador/{id_reciclador}/punto_reciclaje/{id}
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
	 * POST /reciclador/{id_reciclador}/punto_reciclaje
	 */
	public ApiResponse post(Context ctx) {
		return mock.toBuilder().recicladorId(Integer.parseInt(ctx.pathParam("id_reciclador"))).build();
	}

	/**
	 * POST /reciclador/{id_reciclador}/punto_reciclaje/{id}/notificacion/{id_residuo}
	 */
	public ApiResponse notificacion(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Integer.parseInt(ctx.pathParam("id_reciclador")))
			.build();
	}

	/**
	 * GET /puntos_reciclaje
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

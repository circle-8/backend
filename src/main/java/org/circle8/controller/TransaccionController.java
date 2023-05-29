package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.ResiduoResponse;
import org.circle8.response.TransaccionResponse;

import java.time.LocalDateTime;
import java.util.List;

public class TransaccionController {
	private final TransaccionResponse mock = TransaccionResponse.builder()
		.id(1)
		.fechaCreacion(LocalDateTime.of(2023, 1, 1, 16, 30))
		.puntoReciclajeUri("/reciclador/1/punto_reciclaje/1")
		.puntoReciclajeId(1)
		.build();

	/**
	 * GET /transaccion/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * PUT /transaccion/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * DELETE /transaccion/{id}
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
	 * POST /transaccion
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * PUT /transaccion/{id}/residuo/{id_residuo}
	 */
	public ApiResponse addResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.residuos(List.of(
				ResiduoResponse.builder().id(Integer.parseInt(ctx.pathParam("id_residuo"))).build())
			)
			.build();
	}

	/**
	 * DELETE /transaccion/{id}/residuo/{id_residuo}
	 */
	public ApiResponse removeResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.residuos(List.of())
			.build();
	}

	/**
	 * POST /transaccion/{id}/transporte/{id_transporte}
	 */
	public ApiResponse setTransporte(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.transporteId(Integer.parseInt(ctx.pathParam("id_transporte")))
			.transporteUri("/transporte/"+ctx.pathParam("id_transporte"))
			.build();
	}

	/**
	 * DELETE /transaccion/{id}/transporte/{id_transporte}
	 */
	public ApiResponse unsetTransporte(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.build();
	}

	/**
	 * POST /transaccion/{id}/solicitud_transporte
	 */
	public ApiResponse solicitudTransporte(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.build();
	}

	/**
	 * GET /transacciones
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

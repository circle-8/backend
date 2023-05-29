package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.PuntoResponse;
import org.circle8.response.RecorridoResponse;
import org.circle8.response.ResiduoResponse;
import org.circle8.response.RetiroResponse;

import java.time.LocalDateTime;
import java.util.List;

public class RecorridoController {
	private final RecorridoResponse mock = RecorridoResponse.builder()
		.fechaRetiro(LocalDateTime.of(2023, 1, 1, 16, 30))
		.recicladorId(1).recicladorUri("/reciclador/1")
		.zonaId(1).zonaUri("/organizacion/1/zona/1")
		.puntoInicio(new PuntoResponse(-34.6347176f,-58.5587959f))
		.puntoFin(new PuntoResponse(-34.6516556f,-58.5356009f))
		.puntos(List.of(
			new RetiroResponse(-34.6347176f,-58.5587959f, new ResiduoResponse())
		))
		.build();

	/**
	 * GET /recorrido/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.build();
	}

	/**
	 * POST /organizacion/{id_organizacion}/zona/{id_zona}/recorrido
	 */
	public ApiResponse post(Context ctx) {
		return mock.toBuilder()
			.zonaId(Integer.parseInt(ctx.pathParam("id_zona")))
			.zonaUri("/organizacion/"+ctx.pathParam("id_organizacion")+"/zona/"+ctx.pathParam("id_zona"))
			.build();
	}

	/**
	 * PUT /organizacion/{id_organizacion}/zona/{id_zona}/recorrido/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.zonaId(Integer.parseInt(ctx.pathParam("id_zona")))
			.zonaUri("/organizacion/"+ctx.pathParam("id_organizacion")+"/zona/"+ctx.pathParam("id_zona"))
			.build();
	}

	/**
	 * DELETE /organizacion/{id_organizacion}/zona/{id_zona}/recorrido/{id}
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
	 * POST /recorrido/{id}/inicio
	 */
	public ApiResponse inicio(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaInicio(LocalDateTime.now())
			.build();
	}

	/**
	 * POST /recorrido/{id}/fin
	 */
	public ApiResponse fin(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaInicio(LocalDateTime.now().minusHours(1))
			.fechaFin(LocalDateTime.now())
			.build();
	}

	/**
	 * GET /recorridos
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

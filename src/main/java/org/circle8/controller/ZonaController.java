package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.PuntoResponse;
import org.circle8.response.TipoResiduoResponse;
import org.circle8.response.ZonaResponse;

import java.util.List;

public class ZonaController {
	private final ZonaResponse mock = ZonaResponse.builder()
		.id(1)
		.polyline(List.of(
			new PuntoResponse(-34.6347176f,-58.5587959f),
			new PuntoResponse(-34.6516556f,-58.5356009f),
			new PuntoResponse(-34.6731596f,-58.5596279f),
			new PuntoResponse(-34.6636766f,-58.5683339f),
			new PuntoResponse(-34.6505856f,-58.5852295f),
			new PuntoResponse(-34.6493356f,-58.5832919f),
			new PuntoResponse(-34.6434332f,-58.5835331f),
			new PuntoResponse(-34.6415567f,-58.5715792f),
			new PuntoResponse(-34.6383786f,-58.5735709f)
		))
		.organizacionUri("/organizacion/1")
		.organizacionId(1)
		.tipoResiduo(List.of(
			new TipoResiduoResponse(1, "ORGANICO"),
			new TipoResiduoResponse(2, "PLASTICO")
		))
		.build();

	/**
	 * GET /organizacion/{id_organizacion}/zona/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Integer.parseInt(ctx.pathParam("id_organizacion")))
			.organizacionUri("/organizacion/"+ctx.pathParam("id_organizacion"))
			.build();
	}

	/**
	 * PUT /organizacion/{id_organizacion}/zona/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Integer.parseInt(ctx.pathParam("id_organizacion")))
			.organizacionUri("/organizacion/"+ctx.pathParam("id_organizacion"))
			.build();
	}

	/**
	 * DELETE /organizacion/{id_organizacion}/zona/{id}
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
	 * POST /organizacion/{id_organizacion}/zona
	 */
	public ApiResponse post(Context ctx) {
		return mock.toBuilder()
			.organizacionId(Integer.parseInt(ctx.pathParam("id_organizacion")))
			.organizacionUri("/organizacion/"+ctx.pathParam("id_organizacion"))
			.build();
	}

	/**
	 * POST /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse includePuntoResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Integer.parseInt(ctx.pathParam("id_organizacion")))
			.organizacionUri("/organizacion/"+ctx.pathParam("id_organizacion"))
			.build();
	}

	/**
	 * DELETE /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse excludePuntoResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Integer.parseInt(ctx.pathParam("id_organizacion")))
			.organizacionUri("/organizacion/"+ctx.pathParam("id_organizacion"))
			.build();
	}

	/**
	 * GET /zonas
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

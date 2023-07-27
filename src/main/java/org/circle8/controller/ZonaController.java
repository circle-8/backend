package org.circle8.controller;

import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.controller.response.ZonaResponse;

import java.util.List;

@Singleton
public class ZonaController {
	private  static final String ID_ORGANIZACION_PARAM = "id_organizacion";
	public static final String ORGANIZACION_URI_BASE = "/organizacion/";

	private final ZonaResponse mock = ZonaResponse.builder()
		.id(1)
		.nombre("Zona 1")
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
		.organizacionId(1L)
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
//			.organizacionId(Integer.parseInt(ctx.pathParam(ID_ORGANIZACION_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ID_ORGANIZACION_PARAM))
			.build();
	}

	/**
	 * PUT /organizacion/{id_organizacion}/zona/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Long.parseLong(ctx.pathParam(ID_ORGANIZACION_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ID_ORGANIZACION_PARAM))
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
			.organizacionId(Long.parseLong(ctx.pathParam(ID_ORGANIZACION_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ID_ORGANIZACION_PARAM))
			.build();
	}

	/**
	 * POST /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse includePuntoResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Long.parseLong(ctx.pathParam(ID_ORGANIZACION_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ID_ORGANIZACION_PARAM))
			.build();
	}

	/**
	 * DELETE /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse excludePuntoResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Long.parseLong(ctx.pathParam(ID_ORGANIZACION_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ID_ORGANIZACION_PARAM))
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

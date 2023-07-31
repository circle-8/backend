package org.circle8.controller;

import java.util.List;

import org.circle8.controller.request.zona.ZonaRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.controller.response.ZonaResponse;
import org.circle8.dto.ZonaDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.ZonaFilter;
import org.circle8.service.ZonaService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ZonaController {
	private ZonaService service;
	
	@Inject
	public ZonaController(ZonaService zonaService) {
		this.service = zonaService;
	}
	
	private  static final String ORGANIZACION_ID_PARAM = "organizacion_id";
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
	 * GET /organizacion/{organizacion_id}/zona/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long organizacionId;
		final long id;		
		try {
			organizacionId = Long.parseLong(ctx.pathParam("organizacion_id"));
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la organización y/o de la zona debe ser numérico", "");
		}
		
		val req = new ZonaRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);
		
		val filter = ZonaFilter.builder()
				.id(id)
				.organizacionId(organizacionId)
				.organizacion(req.organizacion)
				.recorridos(req.recorridos)
				.tiposResiduos(req.tiposResiduo)
				.build();
		
		try {
			return this.service.get(filter).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}, expand={}] error getting zona", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /organizacion/{id_organizacion}/zona/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Long.parseLong(ctx.pathParam(ORGANIZACION_ID_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ORGANIZACION_ID_PARAM))
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
			.organizacionId(Long.parseLong(ctx.pathParam(ORGANIZACION_ID_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ORGANIZACION_ID_PARAM))
			.build();
	}

	/**
	 * POST /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse includePuntoResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Long.parseLong(ctx.pathParam(ORGANIZACION_ID_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ORGANIZACION_ID_PARAM))
			.build();
	}

	/**
	 * DELETE /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse excludePuntoResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.organizacionId(Long.parseLong(ctx.pathParam(ORGANIZACION_ID_PARAM)))
			.organizacionUri(ORGANIZACION_URI_BASE + ctx.pathParam(ORGANIZACION_ID_PARAM))
			.build();
	}

	/**
	 * GET /zonas
	 */
	public ApiResponse list(Context ctx) {
		val req = new ZonaRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);
		
		val filter = ZonaFilter.builder()
				.organizacionId(req.organizacionId)
				.organizacion(req.organizacion)
				.recorridos(req.recorridos)
				.tiposResiduos(req.tiposResiduo)
				.build();
		
		try {
			val zonas = this.service.list(filter);
			return new ListResponse<>(zonas.stream().map(ZonaDto::toResponse).toList());
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

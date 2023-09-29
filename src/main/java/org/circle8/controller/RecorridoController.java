package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.recorrido.PostRecorridoRequest;
import org.circle8.controller.request.recorrido.PutRecorridoRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.dto.RecorridoDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.service.RecorridoService;
import org.circle8.service.RecorridoService.UpdateEnum;

import java.util.List;


@Singleton
@Slf4j
public class RecorridoController {
	private static final String ZONA_ID_PARAM = "zona_id";
	private static final String ORGANIZACION_ID_PARAM = "organizacion_id";

	private final RecorridoService service;

	@Inject
	public RecorridoController(RecorridoService service) {
		this.service = service;
	}

	/**
	 * GET /recorrido/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del recorrido debe ser numérico", "");
		}

		val expand = new RecorridoExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));

		try {
			return this.service.get(id, expand).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}, expand={}] error approve solicitud", id, expand, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /organizacion/{organizacion_id}/zona/{zona_id}/recorrido
	 */
	public ApiResponse post(Context ctx) {
		long zonaId;
		long organizacionId;
		try {
			zonaId = Long.parseLong(ctx.pathParam(ZONA_ID_PARAM));
			organizacionId = Long.parseLong(ctx.pathParam(ORGANIZACION_ID_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids de zona y organizacion deben ser numéricos", "");
		}

		val req = ctx.bodyAsClass(PostRecorridoRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		var dto = RecorridoDto.from(req, zonaId, organizacionId);
		try {
			return service.save(dto).toResponse();
		} catch (ServiceError e) {
			log.error("[Request:{}] error saving new recorrido", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /organizacion/{organizacion_id}/zona/{zona_id}/recorrido/{id}
	 */
	public ApiResponse put(Context ctx) {
		long id;
		long zonaId;
		long organizacionId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			zonaId = Long.parseLong(ctx.pathParam(ZONA_ID_PARAM));
			organizacionId = Long.parseLong(ctx.pathParam(ORGANIZACION_ID_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids de zona y organizacion deben ser numéricos", "");
		}

		val req = ctx.bodyAsClass(PutRecorridoRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		var dto = RecorridoDto.from(req, zonaId, organizacionId, id);
		try {
			return service.update(dto, UpdateEnum.RETIRO).toResponse();
		} catch (ServiceError e) {
			log.error("[Request:{}] error saving new recorrido", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * DELETE /organizacion/{organizacion_id}/zona/{zona_id}/recorrido/{id}
	 */
	public ApiResponse delete(Context ctx) {
		long id;
		long zonaId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			zonaId = Long.parseLong(ctx.pathParam(ZONA_ID_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids de zona y organizacion deben ser numéricos", "");
		}

		try{
			this.service.delete(id, zonaId);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("[id:{}, zonaId:{}] error deleting recorrido", id, zonaId, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /recorrido/{id}/inicio
	 */
	public ApiResponse inicio(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del recorrido debe ser numérico", "");
		}
		try {
			val dto = new RecorridoDto();
			dto.id = id;
			return service.update(dto, UpdateEnum.INICIO).toResponse();
		} catch ( ServiceError e ) {
			log.error("[id:{}, zonaId:{}] error updating recorrido", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch (ServiceException e) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /recorrido/{id}/fin
	 */
	public ApiResponse fin(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del recorrido debe ser numérico", "");
		}
		try {
			val dto = new RecorridoDto();
			dto.id = id;
			return service.update(dto, UpdateEnum.FIN).toResponse();
		} catch ( ServiceError e ) {
			log.error("[id:{}] error finishing recorrido", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch (ServiceException e) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * GET /recorridos
	 */
	public ApiResponse list(Context ctx) {
		val filter = new RecorridoFilter(ctx.queryParamMap());
		val expand = new RecorridoExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));
		try {
			val recorridos = service.list(filter, expand);
			return new ListResponse<>(recorridos.stream().map(RecorridoDto::toResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request: filter={}] error listing recorridos", filter, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}
}

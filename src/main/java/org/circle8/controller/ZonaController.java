package org.circle8.controller;

import java.util.List;

import org.circle8.controller.request.zona.PostPutZonaRequest;
import org.circle8.controller.request.zona.ZonaRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.dto.ZonaDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.ZonaExpand;
import org.circle8.filter.ZonaFilter;
import org.circle8.service.ZonaService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
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
				.tiposResiduos(req.tiposResiduo)
				.build();

		val expand = new ZonaExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));

		try {
			return this.service.get(filter, expand).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}, organizacionId={}, expand={}] error getting zona", id, organizacionId, expand, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /organizacion/{organizacion_id}/zona/{id}
	 */
	public ApiResponse put(Context ctx) {
		final long organizacionId;
		final long id;		
		try {
			organizacionId = Long.parseLong(ctx.pathParam("organizacion_id"));
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la organización y/o de la zona debe ser numérico", "");
		}
		
		val req = ctx.bodyAsClass(PostPutZonaRequest.class);
		val valid = req.valid();
		if ( !valid.valid()) {
			return new ErrorResponse(valid);
		}
		
		val dto = ZonaDto.from(req);
		
		try {
			return service.put(organizacionId,id,dto).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error saving new PuntoReciclaje", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * DELETE /organizacion/{organizacion_id}/zona/{id}
	 */
	public ApiResponse delete(Context ctx) {
		long organizacion_id;
		long zonaId;
		try {
			organizacion_id = Long.parseLong(ctx.pathParam("organizacion_id"));
			zonaId = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids de zona y organizacion deben ser numéricos", "");
		}

		try{
			this.service.delete(organizacion_id, zonaId);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("[organizacion_id:{}, id:{}] error deleting zona", organizacion_id, zonaId, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /organizacion/{organizacion_id}/zona
	 */
	public ApiResponse post(Context ctx) {
		final long organizacionId;
		try {
			organizacionId = Long.parseLong(ctx.pathParam("organizacion_id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la organización debe ser numérico", "");
		}
		
		val req = ctx.bodyAsClass(PostPutZonaRequest.class);
		val valid = req.valid();
		if ( !valid.valid()) {
			return new ErrorResponse(valid);
		}
		
		val dto = ZonaDto.from(req);
		
		try {
			return service.save(organizacionId,dto).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error saving new PuntoReciclaje", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * POST /punto_residuo/{punto_residuo_id}/zona/{id}
	 */
	public ApiResponse includePuntoResiduo(Context ctx) {
		return doIncludeExclude(ctx, true);
	}

	/**
	 * DELETE /punto_residuo/{id_punto_residuo}/zona/{id}
	 */
	public ApiResponse excludePuntoResiduo(Context ctx) {
		return doIncludeExclude(ctx, false);
	}

	private ApiResponse doIncludeExclude(Context ctx,boolean isInclude) {
		final long puntoResiduoId;
		final long zonaId;
		try {
			puntoResiduoId = Long.parseLong(ctx.pathParam("punto_residuo_id"));
			zonaId = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}

		try {
			val dto = isInclude ?
					this.service.includePuntoResiduo(puntoResiduoId, zonaId) :
						this.service.excludePuntoResiduo(puntoResiduoId, zonaId);
			return dto.toResponse();
		} catch (ServiceError e) {
			return new ErrorResponse(e);
		} catch (ServiceException e) {
			return new ErrorResponse(e);
		}
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
				.recicladorId(req.recicladorId)
				.ciudadanoId(req.ciudadanoId)
				.puntoResiduoId(req.puntoResiduoId)
				.tiposResiduos(req.tiposResiduo)
				.build();

		val expand = new ZonaExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));

		try {
			val zonas = this.service.list(filter, expand);
			return new ListResponse<>(zonas.stream().map(ZonaDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list zonas", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

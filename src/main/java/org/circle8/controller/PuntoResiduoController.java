package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.punto_residuo.PuntosResiduosRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.dto.PuntoResiduoDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.filter.PuntoResiduoFilter;
import org.circle8.service.PuntoResiduoService;

import java.util.List;

@Singleton
@Slf4j
public class PuntoResiduoController {
	private final PuntoResiduoService service;

	@Inject
	public PuntoResiduoController(PuntoResiduoService puntoResiduoService) {
		this.service = puntoResiduoService;
	}

	public ApiResponse get(Context ctx) {
		final Long ciudadanoId;
		final Long id;
		try {
			ciudadanoId = Long.parseLong(ctx.pathParam("ciudadano_id"));
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}

		val expand = new PuntoResiduoExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));

		try {
			return service.get(ciudadanoId, id, expand).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: ciudadano_id={}, id={}] error get punto residuo", ciudadanoId, id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}

	public ApiResponse list(Context ctx) {
		val req = new PuntosResiduosRequest(ctx.queryParamMap());
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(valid);

		val filter = PuntoResiduoFilter.builder()
			.ciudadanoId(req.ciudadanoId)
			.latitud(req.latitud).longitud(req.longitud).radio(req.radio)
			.tipoResiduos(req.tipoResiduo)
			.build();

		val expand = new PuntoResiduoExpand(req.expands);

		try {
			val points = service.list(filter, expand);
			return new ListResponse<>(points.stream().map(PuntoResiduoDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list punto residuo", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}
}

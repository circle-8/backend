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
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.dto.PuntoResiduoDto;
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

	public ApiResponse list(Context ctx) {
		val req = new PuntosResiduosRequest(ctx.queryParamMap());
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(valid);

		val filter = PuntoResiduoFilter.builder()
			.latitud(req.latitud).longitud(req.longitud).radio(req.radio)
			.tipoResiduos(req.tipoResiduo)
			.build();

		val expand = new PuntoResiduoExpand(req.expands);

		try {
			val points = service.list(filter, expand);
			return new ListResponse<>(points.stream().map(PuntoResiduoDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error saving new user", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}
}

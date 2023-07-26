package org.circle8.controller;

import org.circle8.controller.request.solicitud.SolicitudRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SolicitudResponse;
import org.circle8.dto.SolicitudDto;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.SolicitudFilter;
import org.circle8.service.SolicitudService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class SolicitudController {
	
	private SolicitudService service;
	
	@Inject
	private SolicitudController(SolicitudService solicitudService) {
		this.service = solicitudService;
	}
	
	private final SolicitudResponse mock = SolicitudResponse.builder()
		.id(1)
		.solicitadoId(20L)
		.solicitadoUri("/user/1")
		.solicitanteId(40L)
		.solicitanteUri("/user/2")
		.estado(EstadoSolicitud.PENDIENTE)
		.build();

	/**
	 * GET /solicitud/{id}
	 */
	public ApiResponse get(Context ctx) {
		final Long id;
		
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la solicitud debe ser numérico", "");
		}	
		
		try {
			var solicitudDto = this.service.get(id);
			return solicitudDto.toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * PUT /solicitud/{id}/aprobar
	 */
	public ApiResponse approve(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.estado(EstadoSolicitud.APROBADA)
			.build();
	}

	/**
	 * PUT /solicitud/{id}/cancelar
	 */
	public ApiResponse cancel(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.estado(EstadoSolicitud.CANCELADA)
			.build();
	}

	/**
	 * GET /solicitudes
	 */
	public ApiResponse list(Context ctx) {
		val req = new SolicitudRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);
		
		val filter = SolicitudFilter.builder()
				.solicitadoId(req.solicitadoId)
				.solicitanteId(req.solicitanteId)
				.build();
		
		try {
			val solicitudes = this.service.list(filter);
			return new ListResponse<>(solicitudes.stream().map(SolicitudDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list solicitudes", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

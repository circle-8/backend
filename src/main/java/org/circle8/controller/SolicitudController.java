package org.circle8.controller;

import java.util.List;

import org.circle8.controller.request.solicitud.SolicitudRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.dto.SolicitudDto;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.SolicitudExpand;
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
	private final SolicitudService service;

	@Inject
	private SolicitudController(SolicitudService solicitudService) {
		this.service = solicitudService;
	}

	/**
	 * GET /solicitud/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la solicitud debe ser numérico", "");
		}

		val expand = new SolicitudExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));

		try {
			var solicitudDto = this.service.get(id, expand);
			return solicitudDto.toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}, expand={}] error approve solicitud", id, expand, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /solicitud/{id}/aprobar
	 */
	public ApiResponse approve(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la solicitud debe ser numérico", "");
		}

		try {
			return this.service.put(id,null,EstadoSolicitud.APROBADA).toResponse();
		} catch (ServiceError e) {
			log.error("[Request: id={}] error approve solicitud", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /solicitud/{id}/cancelar
	 */
	public ApiResponse cancel(Context ctx) {
		final long id;
		final long ciudadanoCancelaId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			var param = ctx.queryParam("ciudadanoCancelaId");
			ciudadanoCancelaId = Long.parseLong(param);
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la solicitud o el id del ciudadano debe ser numérico", e.getMessage());
		}

		try {
			return this.service.put(id, ciudadanoCancelaId, EstadoSolicitud.CANCELADA).toResponse();
		} catch (ServiceError e) {
			log.error("[Request: id={}, ciudadanoCancelaId={}] error cancel solicitud", id, ciudadanoCancelaId, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
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

		val expand = new SolicitudExpand(req.expands);

		try {
			val solicitudes = this.service.list(filter, expand);
			return new ListResponse<>(solicitudes.stream().map(SolicitudDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list solicitudes", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

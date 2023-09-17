package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.service.OrganizacionService;
import org.circle8.service.RecicladorUrbanoService;

@Singleton
@Slf4j
public class OrganizacionController {
	private final OrganizacionService service;
	private final RecicladorUrbanoService recicladorService;

	@Inject public OrganizacionController(
		OrganizacionService service,
		RecicladorUrbanoService recicladorService
	) {
		this.service = service;
		this.recicladorService = recicladorService;
	}

	/**
	 * GET /organizacion/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de la organizacion debe ser numérico", "");
		}

		try {
			return this.service.get(id).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error get organizacion", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * DELETE /organizacion/{organizacion_id}/reciclador/{reciclador_id}
	 * Hace una baja logica del reciclador. Sacandole la zona.
	 * Se le puede dar de alta nuevamente con el PUT de user, agregandole una zona.
	 */
	public ApiResponse removeReciclador(Context ctx) {
		final long organizacionId;
		final long recicladorId;

		try {
			organizacionId = Long.parseLong(ctx.pathParam("organizacion_id"));
			recicladorId = Long.parseLong(ctx.pathParam("reciclador_id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numericos", "");
		}

		try {
			recicladorService.removeZona(recicladorId);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error(
				"[Request: organizacion_id={}, reciclador_id={}] error get organizacion",
				organizacionId,
				recicladorId,
				e
			);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}
}

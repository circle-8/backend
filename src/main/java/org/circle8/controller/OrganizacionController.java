package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.service.OrganizacionService;

@Singleton
@Slf4j
public class OrganizacionController {
	private final OrganizacionService service;

	@Inject public OrganizacionController(OrganizacionService service) { this.service = service; }

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
}

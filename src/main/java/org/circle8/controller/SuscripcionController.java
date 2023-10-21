package org.circle8.controller;

import java.time.LocalDate;

import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SuscripcionResponse;
import org.circle8.dto.SuscripcionDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.SuscripcionFilter;
import org.circle8.service.SuscripcionService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class SuscripcionController {
	private final SuscripcionService service;
	
	@Inject
	private SuscripcionController(SuscripcionService service) {
		this.service = service;
	}
	
	private final SuscripcionResponse mock = SuscripcionResponse.builder()
		.id(1)
		.ultimaRenovacion(LocalDate.now().minusDays(120))
		.proximaRenovacion(LocalDate.now().plusDays(120))
		.build();
	
	/**
	 * GET /suscripciones
	 */
	public ApiResponse list(Context ctx) {		
		val filter = new SuscripcionFilter(ctx.queryParamMap());
		
		try {
			val suscripciones = service.list(filter);
			return new ListResponse<>(suscripciones.stream().map(SuscripcionDto::toResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request: filter={}] error listing recorridos", filter, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}
	
	/**
	 * GET /user/{user_id}/suscripcion
	 */
	public ApiResponse get(Context ctx) {
		final long userId;
		try {
			userId = Long.parseLong(ctx.pathParam("user_id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del usuario debe ser numérico", "");
		}
		
		try {
			var dto = this.service.get(userId);
			return dto.toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: user_id={}] error get suscripcion", userId, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /user/{id_user}/suscripcion
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * DELETE /user/{id_user}/suscripcion
	 */
	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}
}

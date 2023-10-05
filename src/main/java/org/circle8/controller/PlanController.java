package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PlanResponse;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.service.PlanService;
import org.circle8.service.RecorridoService;

import java.math.BigDecimal;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Singleton
@Slf4j
public class PlanController {

	private final PlanService service;

	@Inject
	public PlanController(PlanService service) {
		this.service = service;
	}

	private final PlanResponse mock = PlanResponse.builder()
		.id(1)
		.nombre("plan prueba")
		.precio(BigDecimal.TEN)
		.mesesRenovacion(3)
		.cantUsuarios(1)
		.build();

	/**
	 * GET /plan/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del plan debe ser numérico", "");
		}
		try {
			return this.service.get(id).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error approve solicitud", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /plan/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /plan
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}

	/**
	 * GET /planes
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

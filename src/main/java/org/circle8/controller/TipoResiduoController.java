package org.circle8.controller;

import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.dto.TipoResiduoDto;
import org.circle8.exception.ServiceError;
import org.circle8.service.TipoResiduoService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.val;

@Singleton

public class TipoResiduoController {
	
	private TipoResiduoService service;
	
	@Inject
	public TipoResiduoController(TipoResiduoService tipoResiduoService) {
		this.service = tipoResiduoService;
	}
	
	private final TipoResiduoResponse mock = new TipoResiduoResponse(1, "ORGANICO");
	/**
	 * GET /tipo_residuo/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * PUT /tipo_residuo/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /tipo_residuo
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * DELETE /tipo_residuo/{id}
	 */
	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}

	/**
	 * GET /tipos_residuo
	 */
	public ApiResponse list(Context ctx) {
		try {
			val l = this.service.list();
			return new ListResponse<>(l.stream().map(TipoResiduoDto::toResponse).toList());
		} catch (ServiceError e) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

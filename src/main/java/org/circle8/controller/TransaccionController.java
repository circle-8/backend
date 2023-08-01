package org.circle8.controller;

import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.controller.response.TransaccionResponse;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.service.TransaccionService;
import org.circle8.utils.Dates;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TransaccionController {

	private final TransaccionService service;
	private static final String ID_TRANSPORTE_PARAM = "id_transporte";
	private static final String ID_RESIDUO_PARAM = "id_residuo";

	@Inject
	public TransaccionController(TransaccionService service) {
		this.service = service;
	}

	private final TransaccionResponse mock = TransaccionResponse.builder()
		.id(1L)
		.fechaCreacion(ZonedDateTime.of(2023, 1, 1, 16, 30, 0, 0, Dates.UTC))
		.puntoReciclajeUri("/reciclador/1/punto_reciclaje/1")
		.puntoReciclajeId(1L)
		.build();

	/**
	 * GET /transaccion/{id}
	 */
	public ApiResponse get(Context ctx) {
		final Long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", e.getMessage());
		}
		try {
			return service.get(id).toResponse();
		} catch (ServiceError e) {
			log.error("[Request: id={}] error get transaccion", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * PUT /transaccion/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Long.parseLong(ctx.pathParam("id"))).build();
	}

	/**
	 * DELETE /transaccion/{id}
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
	 * POST /transaccion
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * PUT /transaccion/{id}/residuo/{id_residuo}
	 */
	public ApiResponse addResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			/*.residuos(List.of(
				ResiduoResponse.builder().id(Long.parseLong(ctx.pathParam(ID_RESIDUO_PARAM))).build())
			)*/
			.build();
	}

	/**
	 * DELETE /transaccion/{id}/residuo/{id_residuo}
	 */
	public ApiResponse removeResiduo(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			//.residuos(List.of())
			.build();
	}

	/**
	 * POST /transaccion/{id}/transporte/{id_transporte}
	 */
	public ApiResponse setTransporte(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.transporteId(Long.parseLong(ctx.pathParam(ID_TRANSPORTE_PARAM)))
			.transporteUri("/transporte/"+ctx.pathParam(ID_TRANSPORTE_PARAM))
			.build();
	}

	/**
	 * DELETE /transaccion/{id}/transporte/{id_transporte}
	 */
	public ApiResponse unsetTransporte(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.build();
	}

	/**
	 * POST /transaccion/{id}/solicitud_transporte
	 */
	public ApiResponse solicitudTransporte(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.build();
	}

	/**
	 * GET /transacciones
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2L).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

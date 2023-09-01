package org.circle8.controller;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.TransporteResponse;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransporteExpand;
import org.circle8.service.TransporteService;
import org.circle8.utils.Dates;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class TransporteController {
	private final TransporteService service;
	
	@Inject
	private TransporteController(TransporteService service) {
		this.service = service;
	}
	
	private final static TransporteResponse mock = TransporteResponse.builder()
		.id(1L)
		.fechaAcordada(ZonedDateTime.of(2023, 1, 1, 16, 30, 0, 0, Dates.UTC))
		.transaccionId(1L)
		.transportistaUri("/transportista/1")
		.build();

	/**
	 * GET /transportes
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2L).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}

	/**
	 * GET /transporte/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del transporte debe ser numérico", "");
		}
		
		val expand = new TransporteExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));
		
		try {
			var dto = this.service.get(id, expand);
			return dto.toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}, expand={}] error get transporte", id, expand, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /transporte/{id}/precio
	 */
	public ApiResponse setPrecio(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.precioAcordado(BigDecimal.TEN)
			.build();
	}

	/**
	 * POST /transporte/{id}/inicio
	 */
	public ApiResponse inicio(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.fechaInicio(ZonedDateTime.now(Dates.UTC))
			.build();
	}

	/**
	 * POST /transporte/{id}/fin
	 */
	public ApiResponse fin(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.fechaInicio(ZonedDateTime.now(Dates.UTC).minusHours(1))
			.fechaFin(ZonedDateTime.now(Dates.UTC))
			.build();
	}

	/**
	 * POST /transporte/{id}/pago
	 */
	public ApiResponse confirmarPago(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.pagoConfirmado(true)
			.build();
	}

	/**
	 * POST /transporte/{id}/confirmacion_entrega
	 */
	public ApiResponse confirmarEntrega(Context ctx) {
		return mock.toBuilder()
			.id(Long.parseLong(ctx.pathParam("id")))
			.entregaConfirmada(true)
			.build();
	}
}

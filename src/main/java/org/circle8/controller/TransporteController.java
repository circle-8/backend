package org.circle8.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.request.transporte.TransporteRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.TransporteResponse;
import org.circle8.dto.TransporteDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.TransporteFilter;
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
		.fechaAcordada(LocalDate.now())
		.transaccionId(1L)
		.transportistaUri("/transportista/1")
		.build();

	/**
	 * GET /transportes
	 */
	public ApiResponse list(Context ctx) {
		val req = new TransporteRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);
		
		val filter = TransporteFilter.builder()
				.userId(req.userId)
				.transportistaId(req.transportistaId)
				.entregaConfirmada(req.entregaConfirmada)
				.pagoConfirmado(req.pagoConfirmado)
				.soloSinTransportista(req.soloSinTransportista)
				.fechaRetiro(req.fechaRetiro)
				.transaccionId(req.transaccionId)
				.build();
		
		val expand = new TransporteExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));
		
		try {
			val transportes = this.service.list(filter, expand);
			return new ListResponse<>(transportes.stream().map(TransporteDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list solicitudes", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}

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
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del transporte debe ser numérico", "");
		}		

		try {
			val tr = new TransporteDto();
			tr.id = id;
			tr.fechaInicio = ZonedDateTime.now(Dates.UTC);
			return this.service.update(tr).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /transporte/{id}/fin
	 */
	public ApiResponse fin(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del transporte debe ser numérico", "");
		}		

		try {
			val tr = new TransporteDto();
			tr.fechaFin = ZonedDateTime.now(Dates.UTC);
			tr.id = id;
			return this.service.update(tr).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /transporte/{id}/pago
	 */
	public ApiResponse confirmarPago(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del transporte debe ser numérico", "");
		}		

		try {
			val tr = new TransporteDto();
			tr.id = id;
			tr.pagoConfirmado = true;
			return this.service.update(tr).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /transporte/{id}/confirmacion_entrega
	 */
	public ApiResponse confirmarEntrega(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del transporte debe ser numérico", "");
		}		

		try {
			val tr = new TransporteDto();
			tr.id = id;
			tr.entregaConfirmada = true;
			return this.service.update(tr).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}
}

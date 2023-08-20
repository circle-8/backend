package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.transaccion.TransaccionPostRequest;
import org.circle8.controller.request.transaccion.TransaccionPutRequest;
import org.circle8.controller.request.transaccion.TransaccionesRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.controller.response.TransaccionResponse;
import org.circle8.dto.TransaccionDto;
import org.circle8.exception.BadRequestException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.TransaccionFilter;
import org.circle8.service.TransaccionService;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;
import java.util.List;

@Singleton
@Slf4j
public class TransaccionController {

	private static final String ID_TRANSPORTE_PARAM = "id_transporte";
	private static final String ID_RESIDUO_PARAM = "id_residuo";

	private final TransaccionService service;

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
		val expand = new TransaccionExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));
		try {
			return service.get(id, expand).toResponse();
		} catch (NotFoundException e) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		} catch (ServiceError e) {
			log.error("[Request: id={}] error get transaccion", id, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * PUT /transaccion/{id}
	 */
	public ApiResponse put(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de transaccion debe ser numérico", "");
		}
		val req = ctx.bodyAsClass(TransaccionPutRequest.class);
		val valid = req.valid();
		req.id = id;
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");
		try {
			return this.service.put(TransaccionDto.from(req)).toResponse();
		} catch (ServiceException e) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * DELETE /transaccion/{id}
	 */
	public ApiResponse delete(Context ctx) {
		final Long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", e.getMessage());
		}
		try{
			service.delete(id);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error delete transaccion: ", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * POST /transaccion
	 */
	public ApiResponse post(Context ctx) {
		val req = new TransaccionPostRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);
		val dto = TransaccionDto.from(req);
		dto.id = 0L;
		try {
			return service.save(dto).toResponse();
		} catch (ServiceError e) {
			log.error("[Request:{}] error saving new Transaccion", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch (NotFoundException e) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
      }
   }

	/**
	 * PUT /transaccion/{id}/residuo/{id_residuo}
	 */
	public ApiResponse addResiduo(Context ctx) {
		final Long id;
		final Long residuoId;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
			residuoId = Long.parseLong(ctx.pathParam(ID_RESIDUO_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}
		try {
			return this.service.put(id, residuoId).toResponse();
		} catch (ServiceException e) {
			log.error("[Request:{}] error put de residuo en transaccion");
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * DELETE /transaccion/{id}/residuo/{id_residuo}
	 */
	public ApiResponse removeResiduo(Context ctx) {
		final Long id;
		final Long residuoId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			residuoId = Long.parseLong(ctx.pathParam(ID_RESIDUO_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}
		try {
			this.service.removeResiduo(id, residuoId);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error delete transaccion: ", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * POST /transaccion/{id}/transporte/{id_transporte}
	 */
	public ApiResponse setTransporte(Context ctx) {
		final Long id;
		final Long transporteId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			transporteId = Long.parseLong(ctx.pathParam(ID_TRANSPORTE_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}
		try {
			this.service.setTransporte(id, transporteId);
			return new SuccessResponse();
		} catch (ServiceError e) {
			log.error("[Request:{}] error setting Transporte to Transaccion: ", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch (NotFoundException e) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
      } catch (BadRequestException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "La Transaccion ya posee un Transporte", "");
		}
   }

	/**
	 * DELETE /transaccion/{id}/transporte/{id_transporte}
	 */
	public ApiResponse unsetTransporte(Context ctx) {
		final Long id;
		final Long transporteId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			transporteId = Long.parseLong(ctx.pathParam(ID_TRANSPORTE_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}
		try {
			this.service.removeTransporte(id, transporteId);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error delete transporteId from transaccion: ", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
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
		val req = new TransaccionesRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);

		val filter = TransaccionFilter.builder().puntosReciclaje(req.puntosReciclaje).transportistaId(req.transportistaId).build();

		val expand = new TransaccionExpand(req.expands);

		try {
			val transactions = service.list(filter, expand);
			return new ListResponse<>(transactions.stream().map(TransaccionDto::toResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request:{}] error list transacciones", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch (ServiceException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}
}

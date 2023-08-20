package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.punto_reciclaje.PuntoReciclajePostRequest;
import org.circle8.controller.request.punto_reciclaje.PuntoReciclajeRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoReciclajeResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.dto.Dia;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.PuntoReciclajeFilter;
import org.circle8.service.PuntoReciclajeService;

import java.util.List;

@Singleton
@Slf4j
public class PuntoReciclajeController {

	private static final String RECICLADOR_ID_PARAM = "reciclador_id";

	private PuntoReciclajeService service;

	@Inject
	public PuntoReciclajeController(PuntoReciclajeService puntoReciclajeService) {
		this.service = puntoReciclajeService;
	}

	private final PuntoReciclajeResponse mock = PuntoReciclajeResponse.builder()
		.id(1)
		.latitud(-34.6701907f).longitud(-58.5656422f)
		.dias(List.of(DiaResponse.LUNES, DiaResponse.MIERCOLES))
		.tipoResiduo(List.of(new TipoResiduoResponse(1, "ORGANICO"), new TipoResiduoResponse(2, "PLASTICO")))
		.build();

	/**
	 * GET /reciclador/{id_reciclador}/punto_reciclaje/{id}
	 */
	public ApiResponse get(Context ctx) {
		final Long id;
		final Long recicladorId;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
			recicladorId = Long.parseLong(ctx.pathParam("reciclador_id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}

		try {
			return this.service.get(id, recicladorId).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * PUT /reciclador/{reciclador_id}/punto_reciclaje/{id}
	 */
	public ApiResponse put(Context ctx) {
		final Long id;
		final Long recicladorId;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
			recicladorId = Long.parseLong(ctx.pathParam(RECICLADOR_ID_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}

		val req = ctx.bodyAsClass(PuntoReciclajePostRequest.class);

		try {
			return this.service.put(id, recicladorId, req).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list puntos reciclaje", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}

	}

	/**
	 * DELETE /reciclador/{reciclador_id}/punto_reciclaje/{id}
	 */
	public ApiResponse delete(Context ctx) {
		final Long id;
		final Long recicladorId;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
			recicladorId = Long.parseLong(ctx.pathParam(RECICLADOR_ID_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", "");
		}
		try{
			this.service.delete(id, recicladorId);

			return new SuccessResponse();

		} catch ( ServiceError e ) {
			log.error("[Request:{}] error delete puntos reciclaje: ", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * POST /reciclador/{reciclador_id}/punto_reciclaje
	 */
	public ApiResponse post(Context ctx) {

		val req = ctx.bodyAsClass(PuntoReciclajePostRequest.class);
		try {
			req.recicladorId = Long.parseLong(ctx.pathParam(RECICLADOR_ID_PARAM));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del reciclador debe ser numérico", "");
		}

		val valid = req.valid();
		if ( !valid.valid()) {
			return new ErrorResponse(valid);
		}

		val dto = PuntoReciclajeDto.from(req);
		try {
			return service.save(dto).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error saving new PuntoReciclaje", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * POST /reciclador/{id_reciclador}/punto_reciclaje/{id}/notificacion/{id_residuo}
	 */
	public ApiResponse notificacion(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Long.parseLong(ctx.pathParam(RECICLADOR_ID_PARAM)))
			.build();
	}

	/**
	 * GET /puntos_reciclaje
	 */
	public ApiResponse list(Context ctx) {
		val req = new PuntoReciclajeRequest(ctx.queryParamMap());
		val valid = req.valid();
		if (!valid.valid())
			return new ErrorResponse(valid);

		val filter = PuntoReciclajeFilter.builder()
			.dias(req.dias.stream().map(Dia::get).toList())
			.tiposResiduos(req.tiposResiduo)
			.reciclador_id(req.recicladorId)
			.latitud(req.latitud).longitud(req.longitud).radio(req.radio)
			.isPuntoVerde(false)
			.build();

		try {
			val l = this.service.list(filter);
			return new ListResponse<>(l.stream().map(PuntoReciclajeDto::toResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request:{}] error list puntos reciclaje", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

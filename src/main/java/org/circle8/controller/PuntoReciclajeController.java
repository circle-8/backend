package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.punto_reciclaje.PuntoReciclajeRequest;
import org.circle8.controller.response.*;
import org.circle8.dto.Dia;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.ServiceError;
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
			var puntoReciclajeDto = this.service.get(id, recicladorId).toResponse();
			if (puntoReciclajeDto == null) {
				return new ErrorResponse(ErrorCode.NOT_FOUND, "El punto de reciclaje no existe", "");
			}

			return puntoReciclajeDto;
		} catch (ServiceError e) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( NotFoundException e ) {
			return new ErrorResponse(ErrorCode.NOT_FOUND, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * PUT /reciclador/{id_reciclador}/punto_reciclaje/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Long.parseLong(ctx.pathParam(RECICLADOR_ID_PARAM)))
			.build();
	}

	/**
	 * DELETE /reciclador/{id_reciclador}/punto_reciclaje/{id}
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
	 * POST /reciclador/{reciclador_id}/punto_reciclaje
	 */
	public ApiResponse post(Context ctx) {

		val req = new PuntoReciclajeRequest(ctx.queryParamMap());
		req.recicladorId = Long.parseLong(ctx.pathParam(RECICLADOR_ID_PARAM));
		val valid = req.validForPost();
		if ( !valid.valid()) {
			return new ErrorResponse(valid);
		}
		val dto = PuntoReciclajeDto.from(req);
		try {
			service.save(dto);
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error saving new PuntoReciclaje", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
		return dto.toResponse();

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
		//TODO cambiar la manera de obtener el tipoResiduo, debe recibir el id y buscar por id, no por nombre
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

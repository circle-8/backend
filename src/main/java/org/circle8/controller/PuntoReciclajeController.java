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
import org.circle8.exception.ServiceError;
import org.circle8.filter.PuntoReciclajeFilter;
import org.circle8.service.PuntoReciclajeService;

import java.util.List;

@Singleton
@Slf4j
public class PuntoReciclajeController {
	private static final String ID_RECICLADOR_PARAM = "id_reciclador";

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
		try {

			val req = new PuntoReciclajeRequest(ctx);
			val valid = req.valid();
			if (!valid.valid())
				return new ErrorResponse(valid);

			var filter = PuntoReciclajeFilter.builder()
				.reciclador_id(req.recicladorId)
				.id(req.id)
				.build();

			var puntoReciclaje = this.service.get(filter);

			if (puntoReciclaje == null) {
				return new ErrorResponse(ErrorCode.NOT_FOUND, "El punto de reciclaje no existe", "");
			}

			return PuntoReciclajeDto.from(puntoReciclaje).toResponse();
		} catch (ServiceError e) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}

	/**
	 * PUT /reciclador/{id_reciclador}/punto_reciclaje/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Long.parseLong(ctx.pathParam(ID_RECICLADOR_PARAM)))
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
	 * POST /reciclador/{id_reciclador}/punto_reciclaje
	 */
	public ApiResponse post(Context ctx) {
		return mock.toBuilder().recicladorId(Long.parseLong(ctx.pathParam(ID_RECICLADOR_PARAM))).build();
	}

	/**
	 * POST /reciclador/{id_reciclador}/punto_reciclaje/{id}/notificacion/{id_residuo}
	 */
	public ApiResponse notificacion(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recicladorId(Long.parseLong(ctx.pathParam(ID_RECICLADOR_PARAM)))
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

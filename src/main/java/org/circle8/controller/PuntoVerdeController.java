package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.punto_reciclaje.PuntoReciclajeRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoVerdeResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.dto.Dia;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.exception.ServiceError;
import org.circle8.filter.PuntoReciclajeFilter;
import org.circle8.service.PuntoReciclajeService;

import java.util.List;

@Singleton
@Slf4j
public class PuntoVerdeController {

	private PuntoReciclajeService service;

	@Inject
	public PuntoVerdeController(PuntoReciclajeService puntoReciclajeService) {
		this.service = puntoReciclajeService;
	}


	private final PuntoVerdeResponse mock = PuntoVerdeResponse.builder()
		.id(1)
		.latitud(-34.6516556f).longitud(-58.5356009f)
		.dias(List.of(DiaResponse.LUNES, DiaResponse.MIERCOLES, DiaResponse.SABADO))
		.tipoResiduo(List.of(
			new TipoResiduoResponse(1, "ORGANICO"),
			new TipoResiduoResponse(2, "PLASTICO")
		))
		.build();
	/**
	 * GET /punto_verde/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * PUT /punto_verde/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /punto_verde
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * DELETE /punto_verde/{id}
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
	 * GET /puntos_verdes
	 */
	public ApiResponse list(Context ctx) {
		val req = new PuntoReciclajeRequest(ctx.queryParamMap());
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(valid);

		val filter = PuntoReciclajeFilter.builder()
				.dias(req.dias.stream().map(Dia::get).toList())
				.tiposResiduos(req.tiposResiduo)
				.latitud(req.latitud).longitud(req.longitud).radio(req.radio)
				.isPuntoVerde(true)
				.build();

		try {
			val l = this.service.list(filter);
			return new ListResponse<>(l.stream().map(PuntoReciclajeDto::toPuntoVerdeResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request:{}] error list puntos verde", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}
	}
}

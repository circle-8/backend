package org.circle8.controller;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoResponse;
import org.circle8.controller.response.RecorridoResponse;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.controller.response.RetiroResponse;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.SolicitudExpand;
import org.circle8.service.RecorridoService;
import org.circle8.utils.Dates;

import com.google.inject.Singleton;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

@Singleton
@Slf4j
public class RecorridoController {
	private static final String ID_ZONA_PARAM = "id_zona";
	private static final String ID_ORGANIZACION_PARAM = "id_organizacion";

	private final RecorridoService service;

	@Inject
	public RecorridoController(RecorridoService service) {
		this.service = service;
	}

	private final RecorridoResponse mock = RecorridoResponse.builder()
		.fechaRetiro(LocalDate.of(2023, 1, 1))
		.recicladorId(1L).recicladorUri("/reciclador/1")
		.zonaId(1L).zonaUri("/organizacion/1/zona/1")
		.puntoInicio(new PuntoResponse(-34.6347176f,-58.5587959f))
		.puntoFin(new PuntoResponse(-34.6516556f,-58.5356009f))
		.puntos(List.of(
			new RetiroResponse(-34.6347176f,-58.5587959f, new ResiduoResponse())
		))
		.build();

	/**
	 * GET /recorrido/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del recorrido debe ser numérico", "");
		}

		val expand = new RecorridoExpand(ctx.queryParamMap().getOrDefault("expand", List.of()));

		try {
			return this.service.get(id, expand).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}, expand={}] error approve solicitud", id, expand, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /organizacion/{id_organizacion}/zona/{id_zona}/recorrido
	 */
	public ApiResponse post(Context ctx) {
		return mock.toBuilder()
			.zonaId(Long.parseLong(ctx.pathParam(ID_ZONA_PARAM)))
			.zonaUri("/organizacion/"+ctx.pathParam(ID_ORGANIZACION_PARAM)+"/zona/"+ctx.pathParam(ID_ZONA_PARAM))
			.build();
	}

	/**
	 * PUT /organizacion/{id_organizacion}/zona/{id_zona}/recorrido/{id}
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.zonaId(Long.parseLong(ctx.pathParam(ID_ZONA_PARAM)))
			.zonaUri("/organizacion/"+ctx.pathParam(ID_ORGANIZACION_PARAM)+"/zona/"+ctx.pathParam(ID_ZONA_PARAM))
			.build();
	}

	/**
	 * DELETE /organizacion/{id_organizacion}/zona/{id_zona}/recorrido/{id}
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
	 * POST /recorrido/{id}/inicio
	 */
	public ApiResponse inicio(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaInicio(ZonedDateTime.now(Dates.UTC))
			.build();
	}

	/**
	 * POST /recorrido/{id}/fin
	 */
	public ApiResponse fin(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaInicio(ZonedDateTime.now(Dates.UTC).minusHours(1))
			.fechaFin(ZonedDateTime.now(Dates.UTC))
			.build();
	}

	/**
	 * GET /recorridos
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

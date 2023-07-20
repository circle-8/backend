package org.circle8.controller;

import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.request.residuo.PostResiduoRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.dto.ResiduoDto;
import org.circle8.exception.ServiceError;
import org.circle8.service.ResiduoService;
import org.circle8.utils.Dates;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ResiduoController {
	
	private final ResiduoService service;
	
	@Inject
	public ResiduoController(ResiduoService service) {
		this.service = service;
	}
	
	private final ResiduoResponse mock = ResiduoResponse.builder()
		.id(1)
		.fechaCreacion(ZonedDateTime.of(2023, 1, 1, 16, 30, 0, 0, Dates.UTC))
		.puntoResiduo(new PuntoResiduoResponse(1, -34.6701907d, -58.5656422d, 1L, "/user/1", null, List.of()))
		.tipoResiduo(new TipoResiduoResponse(1, "ORGANICO"))
		.recorridoUri("/recorrido/1").recorridoId(1L)
		.build();

	/**
	 * GET /residuo/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * PUT /residuo/{id}
	 * Puede cambiar el tipo de residuo
	 */
	public ApiResponse put(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /residuo
	 * Requiere de :
	 * Tipo de Residuo
	 * Punto de Residuo
	 * Fecha limite retiro (opcional)
	 * Descripcion
	 */
	public ApiResponse post(Context ctx) {
		val req = ctx.bodyAsClass(PostResiduoRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");
		
		var dto = ResiduoDto.from(req);		
		try {
			dto = service.save(dto);
		} catch (ServiceError e) {
			log.error("[Request:{}] error saving new residuo", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		}		
		return dto.toResponse();
	}

	public ApiResponse delete(Context ctx) {
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
	}

	/**
	 * GET /residuos
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}

	/**
	 * POST /residuo/{id}/reciclaje
	 */
	public ApiResponse reciclaje(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * DELETE /residuo/{id}/reciclaje
	 */
	public ApiResponse deleteReciclaje(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.recorridoUri("").recorridoId(null)
			.build();
	}

	/**
	 * POST /residuo/{id}/notificacion/{id_punto_reciclaje}
	 */
	public ApiResponse notificacion(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /residuo/{id}/fulfill
	 */
	public ApiResponse fulfill(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaRetiro(ZonedDateTime.now(Dates.UTC))
			.build();
	}

	/**
	 * POST /residuo/{id}/unfulfilled
	 */
	public ApiResponse unfulfilled(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaRetiro(null)
			.build();
	}
}

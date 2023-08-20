package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import org.circle8.exception.ServiceException;
import org.circle8.filter.ResiduosFilter;
import org.circle8.service.ResiduoService;
import org.circle8.service.SolicitudService;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;
import java.util.List;

@Singleton
@Slf4j
public class ResiduoController {

	private final ResiduoService service;
	private final SolicitudService solicitudService;

	@Inject
	public ResiduoController(ResiduoService service, SolicitudService solicitudService) {
		this.service = service;
		this.solicitudService = solicitudService;
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
			return service.save(dto).toResponse();
		} catch (ServiceError e) {
			log.error("[Request:{}] error saving new residuo", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
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
		val filter = new ResiduosFilter(ctx.queryParamMap());
		try {
			val residuos = service.list(filter);
			return new ListResponse<>(residuos.stream().map(ResiduoDto::toResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request: filter={}] error listing residuos", filter, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /residuo/{id}/reciclaje
	 */
	public ApiResponse reciclaje(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id debe ser numérico", e.getMessage());
		}

		try {
			return service.addToRecorrido(id).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error residuo to reciclaje", id, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * DELETE /residuo/{id}/reciclaje
	 */
	public ApiResponse deleteReciclaje(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id debe ser numérico", e.getMessage());
		}

		try {
			return service.deleteFromRecorrido(id).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error residuo delete from reciclaje", id, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /residuo/{id}/notificacion/{punto_reciclaje_id}
	 */
	public ApiResponse notificacion(Context ctx) {
		return doNotificacion(ctx, SolicitudService.TipoSolicitud.RETIRO);
	}

	/**
	 * POST /residuo/{id}/notificacion/deposito/{punto_reciclaje_id}
	 */
	public ApiResponse notificacionDeposito(Context ctx) {
		return doNotificacion(ctx, SolicitudService.TipoSolicitud.DEPOSITO);
	}

	private ApiResponse doNotificacion(Context ctx, SolicitudService.TipoSolicitud tipoSolicitud) {
		final long id;
		final long puntoReciclajeId;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
			puntoReciclajeId = Long.parseLong(ctx.pathParam("punto_reciclaje_id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "Los ids deben ser numéricos", e.getMessage());
		}

		try {
			return solicitudService
				.save(id, puntoReciclajeId, tipoSolicitud)
				.toResponse();
		} catch (ServiceError e) {
			log.error(
				"[Request: puntoReciclajeId={}, id={}, tipo={}] error save notificacion",
				puntoReciclajeId,
				id,
				tipoSolicitud,
				e
			);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /residuo/{id}/fulfill
	 */
	public ApiResponse fulfill(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id deben ser numérico", e.getMessage());
		}

		try {
			return service.fulfill(id).toResponse();
		} catch (ServiceError e) {
			log.error("[Request: id={}] error fulfill", id, e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
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

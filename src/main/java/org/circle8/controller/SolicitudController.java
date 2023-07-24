package org.circle8.controller;

import io.javalin.http.Context;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SolicitudResponse;
import org.circle8.entity.EstadoSolicitud;

import java.util.List;

public class SolicitudController {
	private final SolicitudResponse mock = SolicitudResponse.builder()
		.id(1)
		.solicitadoId(20L)
		.solicitadoUri("/user/1")
		.solicitanteId(40L)
		.solicitanteUri("/user/2")
		.estado(EstadoSolicitud.PENDIENTE)
		.build();

	/**
	 * GET /solicitud/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.build();
	}

	/**
	 * PUT /solicitud/{id}/aprobar
	 */
	public ApiResponse approve(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.estado(EstadoSolicitud.APROBADA)
			.build();
	}

	/**
	 * PUT /solicitud/{id}/cancelar
	 */
	public ApiResponse cancel(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.estado(EstadoSolicitud.CANCELADA)
			.build();
	}

	/**
	 * GET /solicitudes
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).estado(EstadoSolicitud.APROBADA).build(),
			mock.toBuilder().id(3).estado(EstadoSolicitud.CANCELADA).build(),
			mock.toBuilder().id(4).estado(EstadoSolicitud.EXPIRADA).build(),
			mock.toBuilder().id(4).estado(EstadoSolicitud.CANCELADA).canceladorId(40L).build()
		);

		return new ListResponse<>(l);
	}
}

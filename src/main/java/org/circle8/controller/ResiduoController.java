package org.circle8.controller;

import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.controller.response.TipoResiduoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class ResiduoController {
	private final ResiduoResponse mock = ResiduoResponse.builder()
		.id(1)
		.fechaCreacion(LocalDateTime.of(2023, 1, 1, 16, 30))
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
	 * Requiere de Tipo de Residuo y Punto de Residuo
	 */
	public ApiResponse post(Context ctx) {
		return mock;
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
			.fechaRetiro(LocalDateTime.now())
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

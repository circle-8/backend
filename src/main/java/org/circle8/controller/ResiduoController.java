package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.PuntoResiduoResponse;
import org.circle8.response.ResiduoResponse;
import org.circle8.response.TipoResiduoResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ResiduoController {
	/**
	 * GET /residuo/{id}
	 */
	public ApiResponse get(Context ctx) {
		return new ResiduoResponse(
			Integer.parseInt(ctx.pathParam("id")),
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			"/recorrido/1",
			1,
			null,
			"/transaccion/1",
			1,
			null
		);
	}

	/**
	 * PUT /residuo/{id}
	 * Puede cambiar el tipo de residuo
	 */
	public ApiResponse put(Context ctx) {
		return new ResiduoResponse(
			Integer.parseInt(ctx.pathParam("id")),
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			"/recorrido/1",
			1,
			null,
			"/transaccion/1",
			1,
			null
		);
	}

	/**
	 * POST /residuo
	 * Requiere de Tipo de Residuo y Punto de Residuo
	 */
	public ApiResponse post(Context ctx) {
		return new ResiduoResponse(
			1,
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			"/recorrido/1",
			1,
			null,
			"/transaccion/1",
			1,
			null
		);
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
	 * POST /residuo
	 * Requiere de Tipo de Residuo y Punto de Residuo
	 */
	public ApiResponse list(Context ctx) {
		List<ResiduoResponse> l = Arrays.asList(
			new ResiduoResponse(
				1,
				null,
				LocalDateTime.of(2023, 1, 1, 16, 30),
				null,
				null,
				new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
				new TipoResiduoResponse(1, "ORGANICO"),
				"/recorrido/1",
				1,
				null,
				"/transaccion/1",
				1,
				null
			),
			new ResiduoResponse(
				2,
				null,
				LocalDateTime.of(2023, 1, 1, 16, 30),
				null,
				null,
				new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
				new TipoResiduoResponse(2, "PLASTICO"),
				"/recorrido/1",
				1,
				null,
				null,
				null,
				null
			)
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}

	public ApiResponse reciclaje(Context context) {
		return new ResiduoResponse(
			1,
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			"/recorrido/1",
			1,
			null,
			null,
			null,
			null
		);
	}

	public ApiResponse deleteReciclaje(Context context) {
		return new ResiduoResponse(
			1,
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

	public ApiResponse notificacion(Context context) {
		return new ResiduoResponse(
			1,
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

	public ApiResponse fulfill(Context context) {
		return new ResiduoResponse(
			1,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			null,
			null,
			null,
			null,
			null,
			null
		);
	}

	public ApiResponse unfulfilled(Context context) {
		return new ResiduoResponse(
			1,
			null,
			LocalDateTime.of(2023, 1, 1, 16, 30),
			null,
			null,
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new TipoResiduoResponse(1, "ORGANICO"),
			null,
			null,
			null,
			null,
			null,
			null
		);
	}
}

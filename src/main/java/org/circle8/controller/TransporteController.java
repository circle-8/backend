package org.circle8.controller;

import com.google.inject.Singleton;
import io.javalin.http.Context;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.TransporteResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class TransporteController {
	private final static TransporteResponse mock = TransporteResponse.builder()
		.id(1)
		.fechaAcordada(LocalDateTime.of(2023, 1, 1, 16, 30))
		.transaccionId(1)
		.transportistaUri("/transportista/1")
		.build();

	/**
	 * GET /transportes
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}

	/**
	 * GET /transporte/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /transporte/{id}/precio
	 */
	public ApiResponse setPrecio(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.precioAcordado(BigDecimal.TEN)
			.build();
	}

	/**
	 * POST /transporte/{id}/inicio
	 */
	public ApiResponse inicio(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaInicio(LocalDateTime.now())
			.build();
	}

	/**
	 * POST /transporte/{id}/fin
	 */
	public ApiResponse fin(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.fechaInicio(LocalDateTime.now().minusHours(1))
			.fechaFin(LocalDateTime.now())
			.build();
	}

	/**
	 * POST /transporte/{id}/pago
	 */
	public ApiResponse confirmarPago(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.pagoConfirmado(true)
			.build();
	}

	/**
	 * POST /transporte/{id}/confirmacion_entrega
	 */
	public ApiResponse confirmarEntrega(Context ctx) {
		return mock.toBuilder()
			.id(Integer.parseInt(ctx.pathParam("id")))
			.entregaConfirmada(true)
			.build();
	}
}

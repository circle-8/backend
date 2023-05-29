package org.circle8.controller;

import io.javalin.http.Context;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.PuntoResiduoResponse;

import java.util.List;

public class PuntoResiduoController {
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			new PuntoResiduoResponse(1, -34.6701907f, -58.5656422f),
			new PuntoResiduoResponse(2, -34.6701900f, -58.5656430f)
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}
}

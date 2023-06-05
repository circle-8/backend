package org.circle8.controller.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PuntoResponse implements ApiResponse {
	public float latitud;
	public float longitud;
}

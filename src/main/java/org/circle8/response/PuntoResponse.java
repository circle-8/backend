package org.circle8.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PuntoResponse implements ApiResponse {
	public float latitud;
	public float longitud;
}

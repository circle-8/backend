package org.circle8.controller.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RetiroResponse implements ApiResponse {
	public float latitud;
	public float longitud;
	public ResiduoResponse residuo;
}

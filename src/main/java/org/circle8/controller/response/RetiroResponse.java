package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RetiroResponse implements ApiResponse {
	public float latitud;
	public float longitud;
	public ResiduoResponse residuo;
}

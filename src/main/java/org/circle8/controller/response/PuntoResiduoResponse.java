package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PuntoResiduoResponse implements ApiResponse {
	public long id;
	public double latitud;
	public double longitud;
}

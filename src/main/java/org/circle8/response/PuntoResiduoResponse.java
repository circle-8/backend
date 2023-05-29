package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PuntoResiduoResponse implements ApiResponse {
	public int id;
	public float latitud;
	public float longitud;
}

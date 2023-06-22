package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoResiduoResponse implements ApiResponse {
	public int id;
	public String nombre;
}

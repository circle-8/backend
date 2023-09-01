package org.circle8.controller.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransportistaResponse implements ApiResponse {
	public Long id;
	public String nombre;
	public String username;
	public List<PuntoResponse> polylineAlcance;
}

package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PuntoResiduoResponse implements ApiResponse {
	public long id;
	public Double latitud;
	public Double longitud;
	public Long ciudadanoId;
	public String ciudadanoUri;
	public UserResponse ciudadano;
	public List<ResiduoResponse> residuos; // TODO: not implemented for list
}

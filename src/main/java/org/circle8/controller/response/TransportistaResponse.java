package org.circle8.controller.response;

import java.util.List;

public class TransportistaResponse implements ApiResponse {
	public int id;
	public String nombre;
	public String username;
	public List<PuntoResponse> polylineAlcance;
}

package org.circle8.response;

import java.util.List;

public class PuntoVerdeResponse implements ApiResponse {
	public int id;
	public float latitud;
	public float longitud;
	public List<DiaResponse> dias;
	public List<TipoResiduoResponse> tipoResiduo;
}

package org.circle8.response;

import java.util.List;

public class PuntoReciclajeResponse implements ApiResponse {
	public int id;
	public float latitud;
	public float longitud;
	public List<DiaResponse> dias;
	public List<TipoResiduoResponse> tipoResiduo;
	public String recicladorUri;
	public Integer recicladorId;
	public CiudadanoResponse reciclador;
}

package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

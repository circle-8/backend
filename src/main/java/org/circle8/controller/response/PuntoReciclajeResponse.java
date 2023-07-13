package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PuntoReciclajeResponse implements ApiResponse {
	public long id;
	public String titulo;
	public double latitud;
	public double longitud;
	public List<DiaResponse> dias;
	public List<TipoResiduoResponse> tipoResiduo;
	public String recicladorUri;
	public Long recicladorId;
	public CiudadanoResponse reciclador;
}

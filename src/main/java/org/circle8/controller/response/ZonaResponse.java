package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ZonaResponse implements ApiResponse {
	public int id;
	public String nombre;
	public List<PuntoResponse> polyline;
	public String organizacionUri;
	public Integer organizacionId;
	public OrganizacionResponse organizacion;
	public List<TipoResiduoResponse> tipoResiduo;
}

package org.circle8.response;

import java.util.List;

public class ZonaResponse implements ApiResponse {
	public int id;
	public String nombre;
	public List<PuntoResponse> polyline;
	public String organizacionUri;
	public Integer organizacionId;
	public OrganizacionResponse organizacion;
	public TipoResiduoResponse tipoResiduo;
}

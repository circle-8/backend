package org.circle8.filter;

import lombok.Builder;

import java.util.List;

@Builder
public class PuntoResiduoFilter {
	public Double latitud;
	public Double longitud;
	public Double radio;
	public Long ciudadanoId;
	public Long notCiudadanoId;
	public List<String> tipoResiduos;

	public boolean hasArea() {
		return latitud != null && longitud != null && radio != null;
	}

	public boolean hasTipo() {
		return !tipoResiduos.isEmpty();
	}
}

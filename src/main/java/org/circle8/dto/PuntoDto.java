package org.circle8.dto;

import org.circle8.controller.response.PuntoResponse;
import org.circle8.entity.Punto;

public class PuntoDto {
	public float latitud;
	public float longitud;
	
	public static PuntoDto from(Punto entity) {
		if ( entity == null ) return null;
		var p = new PuntoDto();
		p.latitud = entity.latitud;
		p.longitud = entity.longitud;
		return p;
	}
	
	public PuntoResponse toResponse() {
		return new PuntoResponse(latitud, longitud);
	}
}

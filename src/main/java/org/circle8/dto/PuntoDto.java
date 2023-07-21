package org.circle8.dto;

import org.circle8.entity.Punto;

public class PuntoDto {
	public float latitud;
	public float longitud;
	
	public static PuntoDto from(Punto entity) {
		var p = new PuntoDto();
		p.latitud = entity.latitud;
		p.longitud = entity.longitud;
		return p;
	}
}

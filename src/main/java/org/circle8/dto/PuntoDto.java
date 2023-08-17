package org.circle8.dto;

import org.circle8.controller.request.recorrido.PuntoRequest;
import org.circle8.controller.response.PuntoResponse;
import org.circle8.entity.Punto;

public class PuntoDto {
	public double latitud;
	public double longitud;

	public static PuntoDto from(Punto entity) {
		if ( entity == null ) return null;
		var p = new PuntoDto();
		p.latitud = entity.latitud;
		p.longitud = entity.longitud;
		return p;
	}

	public static PuntoDto from(PuntoRequest req) {
		if ( req == null ) return null;
		var p = new PuntoDto();
		p.latitud = req.latitud;
		p.longitud = req.longitud;
		return p;
	}

	public Punto toEntity() {
		return new Punto(this.latitud, this.longitud);
	}

	public PuntoResponse toResponse() {
		return new PuntoResponse(latitud, longitud);
	}
}

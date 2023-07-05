package org.circle8.dto;

import lombok.val;
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.entity.PuntoResiduo;

public class PuntoResiduoDto {
	public long id;
	public double latitud;
	public double longitud;

	public static PuntoResiduoDto from(PuntoResiduo entity) {
		val p = new PuntoResiduoDto();
		p.id = entity.id;
		p.latitud = entity.latitud;
		p.longitud = entity.longitud;
		return p;
	}

	public PuntoResiduoResponse toResponse() {
		val r = new PuntoResiduoResponse();
		r.id = this.id;
		r.latitud = this.latitud;
		r.longitud = this.longitud;
		return r;
	}
}

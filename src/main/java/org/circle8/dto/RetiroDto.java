package org.circle8.dto;

import org.circle8.controller.response.RetiroResponse;
import org.circle8.entity.Retiro;

public class RetiroDto {
	public float latitud;
	public float longitud;
	public ResiduoDto residuo;

	public static RetiroDto from(Retiro entity) {
		if ( entity == null ) return null;
		var r = new RetiroDto();
		r.latitud = entity.latitud;
		r.longitud = entity.longitud;
		r.residuo = ResiduoDto.from(entity.residuo);
		return r;
	}
	
	public RetiroResponse toResponse() {
		var r = new RetiroResponse();
		r.latitud = this.latitud;
		r.longitud = this.longitud;
		r.residuo = this.residuo != null ?
				this.residuo.toResponse() : null;		
		return r;
	}
}

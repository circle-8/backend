package org.circle8.dto;

import org.circle8.entity.Retiro;

public class RetiroDto {
	public float latitud;
	public float longitud;
	public ResiduoDto residuo;
	
	public static RetiroDto from(Retiro entity) {
		var r = new RetiroDto();
		r.latitud = entity.latitud;
		r.longitud = entity.longitud;
		r.residuo = ResiduoDto.from(entity.residuo);
		return r;
	}
}

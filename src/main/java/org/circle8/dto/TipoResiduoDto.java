package org.circle8.dto;

import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.entity.TipoResiduo;

public class TipoResiduoDto {
	public int id;
	public String nombre;
	
	public static TipoResiduoDto from(TipoResiduo entity) {
		var tr = new TipoResiduoDto();
		tr.id = entity.id;
		tr.nombre = entity.nombre;
		return tr;
	}	
	
	public TipoResiduoResponse toResponse() {
		return new TipoResiduoResponse(id, nombre);
	}
	
	public TipoResiduo toEntity() {
		return TipoResiduo.builder()
				.id(id)
				.nombre(nombre)
				.build();
	}
	
	
}

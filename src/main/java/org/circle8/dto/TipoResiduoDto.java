package org.circle8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.response.TipoResiduoResponse;
import org.circle8.entity.TipoResiduo;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoResiduoDto {
	public long id;
	public String nombre;

	public static TipoResiduoDto from(TipoResiduo entity) {
		if ( entity == null ) return null;
		var tr = new TipoResiduoDto();
		tr.id = entity.id;
		tr.nombre = entity.nombre;
		return tr;
	}

	public static TipoResiduoDto from(Integer tipoResiduo) {
		var tr = new TipoResiduoDto();
		tr.id = tipoResiduo;
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

package org.circle8.dto;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<TipoResiduoDto> from(List<TipoResiduo> listEntity){
		List<TipoResiduoDto> lista = new ArrayList<TipoResiduoDto>();
		for(TipoResiduo t : listEntity) {
			lista.add(TipoResiduoDto.from(t));
		}
		return lista;
	}
	
	public static List<TipoResiduoResponse> toResponse(List<TipoResiduoDto> lista){
		List<TipoResiduoResponse> l = new ArrayList<TipoResiduoResponse>();
		for(TipoResiduoDto t : lista){
			l.add(t.toResponse());
		}
		return l;
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

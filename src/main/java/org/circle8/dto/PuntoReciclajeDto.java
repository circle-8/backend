package org.circle8.dto;

import java.util.List;

import org.circle8.controller.response.PuntoReciclajeResponse;
import org.circle8.entity.PuntoReciclaje;

public class PuntoReciclajeDto {
	public int id;
	public float latitud;
	public float longitud;
	public List<Dia> dias;
	public List<TipoResiduoDto> tipoResiduo;
	public String recicladorUri;
	public Integer recicladorId;
	public CiudadanoDto reciclador;
	
	
//	public static PuntoReciclajeDto from() {
//		var pr = new PuntoReciclajeDto();
//		return pr;
//	}
	
	public static PuntoReciclajeDto from(PuntoReciclaje entity) {
		var pr = new PuntoReciclajeDto();
		pr.id = entity.id;
		pr.latitud = entity.latitud;
		pr.longitud = entity.longitud;
		pr.dias = entity.dias;
		pr.tipoResiduo = TipoResiduoDto.from(entity.tipoResiduo);
		pr.recicladorUri = entity.recicladorUri;
		pr.recicladorId = entity.recicladorId;
		pr.reciclador = entity.reciclador != null ? CiudadanoDto.from(entity.reciclador) : null;
		return pr;
	}
	
	public PuntoReciclajeResponse toResponce() {
		return new PuntoReciclajeResponse(id, latitud, longitud, null,
				TipoResiduoDto.toResponse(tipoResiduo), recicladorUri, 
				recicladorId, reciclador != null ?  reciclador.toResponse() : null);
	}
	
	public PuntoReciclaje toEntity() {
		return PuntoReciclaje.builder()
				.id(id)
				.latitud(latitud)
				.longitud(longitud)
				.dias(dias)
				.recicladorUri(recicladorUri)
				.recicladorId(recicladorId)
				.reciclador(reciclador.toEntity())
				.build();
	}
}

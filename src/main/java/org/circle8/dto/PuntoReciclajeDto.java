package org.circle8.dto;

import java.util.List;

import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.PuntoReciclajeResponse;
import org.circle8.entity.PuntoReciclaje;

public class PuntoReciclajeDto {
	public long id;
	public String titulo;
	public double latitud;
	public double longitud;
	public List<Dia> dias;
	public List<TipoResiduoDto> tipoResiduo;
	public String recicladorUri;
	public Long recicladorId;
	public CiudadanoDto reciclador;
	
	public static PuntoReciclajeDto from(PuntoReciclaje entity) {
		var pr = new PuntoReciclajeDto();
		pr.id = entity.id;
		pr.titulo = entity.titulo;
		pr.latitud = entity.latitud;
		pr.longitud = entity.longitud;
		pr.dias = entity.dias;
		pr.tipoResiduo = entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList();
		pr.recicladorUri = entity.recicladorUri;
		pr.recicladorId = entity.recicladorId;
		pr.reciclador = entity.reciclador != null ? CiudadanoDto.from(entity.reciclador) : null;
		return pr;
	}
	
	public PuntoReciclajeResponse toResponse() {
		return new PuntoReciclajeResponse(id, titulo, latitud, longitud, 
				dias.stream().map(DiaResponse::from).toList(),
				tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList(), recicladorUri, 
				recicladorId, reciclador != null ?  reciclador.toResponse() : null);
	}
	
	public PuntoReciclaje toEntity() {
		return PuntoReciclaje.builder()
				.id(id)
				.titulo(titulo)
				.latitud(latitud)
				.longitud(longitud)
				.dias(dias)
				.recicladorUri(recicladorUri)
				.recicladorId(recicladorId)
				.reciclador(reciclador.toEntity())
				.build();
	}
}

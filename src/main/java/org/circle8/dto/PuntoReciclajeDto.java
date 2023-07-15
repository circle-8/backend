package org.circle8.dto;

import java.util.List;

import org.circle8.controller.request.punto_reciclaje.PuntoReciclajeRequest;
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
	public Long recicladorId;
	public UserDto reciclador;

	public static PuntoReciclajeDto from(PuntoReciclaje entity) {
		var pr = new PuntoReciclajeDto();
		pr.id = entity.id;
		pr.titulo = entity.titulo;
		pr.latitud = entity.latitud;
		pr.longitud = entity.longitud;
		pr.dias = entity.dias;
		pr.tipoResiduo = entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList();
		pr.recicladorId = entity.recicladorId;
		pr.reciclador = entity.reciclador != null ? UserDto.from(entity.reciclador) : null;
		return pr;
	}

	public static PuntoReciclajeDto from(PuntoReciclajeRequest request) {
		var pr = new PuntoReciclajeDto();
		pr.titulo = request.titulo;
		pr.latitud = request.latitud;
		pr.longitud = request.longitud;
		pr.dias = Dia.getDia(request.dias);
		pr.tipoResiduo = request.tiposResiduo.stream().map(TipoResiduoDto::from).toList();
		pr.recicladorId = request.recicladorId;
		return pr;
	}

	public PuntoReciclajeResponse toResponse() {
		return new PuntoReciclajeResponse(
			id,
			titulo,
			latitud,
			longitud,
			dias.stream().map(DiaResponse::from).toList(),
			tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList(),
			"/user/" + recicladorId,
			recicladorId,
			reciclador != null ?  reciclador.toResponse() : null
		);
	}

	public PuntoReciclaje toEntity() {
		return PuntoReciclaje.builder()
									.id(id).titulo(titulo).latitud(latitud).longitud(longitud)
									.dias(dias).tipoResiduo(tipoResiduo.stream().map(TipoResiduoDto::toEntity).toList())
									.recicladorId(recicladorId).build();
	}
}

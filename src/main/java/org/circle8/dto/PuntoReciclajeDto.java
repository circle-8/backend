package org.circle8.dto;

import java.util.List;

import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.PuntoReciclajeResponse;
import org.circle8.controller.response.PuntoVerdeResponse;
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

	public PuntoReciclajeResponse toResponse() {
		return new PuntoReciclajeResponse(
			id,
			titulo,
			latitud,
			longitud,
			dias.stream().map(DiaResponse::from).toList(),
			tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList(),
			"/user/" + reciclador.id,
			recicladorId,
			reciclador.toResponse()
		);
	}

	public PuntoVerdeResponse toPuntoVerdeResponse() {
		return new PuntoVerdeResponse(
				id,
				titulo,
				latitud,
				longitud,
				dias.stream().map(DiaResponse::from).toList(),
				tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList()
			);
	}
}

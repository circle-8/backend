package org.circle8.dto;

import java.util.List;

import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.PuntoVerdeResponse;
import org.circle8.entity.PuntoVerde;

public class PuntoVerdeDto {
	public long id;
	public String titulo;
	public double latitud;
	public double longitud;
	public List<Dia> dias;
	public List<TipoResiduoDto> tipoResiduo;

	public static PuntoVerdeDto from(PuntoVerde entity) {
		var pr = new PuntoVerdeDto();
		pr.id = entity.id;
		pr.titulo = entity.titulo;
		pr.latitud = entity.latitud;
		pr.longitud = entity.longitud;
		pr.dias = entity.dias;
		pr.tipoResiduo = entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList();
		return pr;
	}

	public PuntoVerdeResponse toResponse() {
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

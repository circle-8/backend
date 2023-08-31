package org.circle8.dto;

import java.util.List;

import org.circle8.controller.response.TransportistaResponse;
import org.circle8.entity.Transportista;

public class TransportistaDto {
	public long id;
	public long usuarioId;
	public List<PuntoDto> polyline;

	public static TransportistaDto from(Transportista entity) {
		if ( entity == null ) return null;
		var t = new TransportistaDto();
		t.id = entity.id;
		t.usuarioId = entity.usuarioId;
		t.polyline = entity.polyline != null
				? entity.polyline.stream().map(PuntoDto::from).toList()
				: List.of();
		return t;
	}

	public TransportistaResponse toResponse() {
		//TODO: implementar
		return new TransportistaResponse();
	}

	public Transportista toEntity() {
		return new Transportista(usuarioId);
	}
}

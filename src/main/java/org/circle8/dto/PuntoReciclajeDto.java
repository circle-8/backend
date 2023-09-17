package org.circle8.dto;

import org.circle8.controller.request.punto_reciclaje.PuntoReciclajePostRequest;
import org.circle8.controller.response.DiaResponse;
import org.circle8.controller.response.PuntoReciclajeResponse;
import org.circle8.controller.response.PuntoVerdeResponse;
import org.circle8.entity.PuntoReciclaje;

import java.util.List;

public class PuntoReciclajeDto {
	public long id;
	public String titulo;
	public double latitud;
	public double longitud;
	public List<Dia> dias;
	public List<TipoResiduoDto> tipoResiduo;
	public String email;
	public Long recicladorId;
	public UserDto reciclador;

	public static PuntoReciclajeDto from(PuntoReciclaje entity) {
		if ( entity == null ) return null;
		var pr = new PuntoReciclajeDto();
		pr.id = entity.id;
		pr.titulo = entity.titulo;
		pr.latitud = entity.latitud;
		pr.longitud = entity.longitud;
		pr.dias = entity.dias;
		pr.tipoResiduo = entity.tipoResiduo != null ? entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList() : List.of();
		pr.recicladorId = entity.recicladorId;
		pr.reciclador = entity.reciclador != null ? UserDto.from(entity.reciclador) : null;
		pr.email = entity.email;
		return pr;
	}

	public static PuntoReciclajeDto from(PuntoReciclajePostRequest request) {
		var pr = new PuntoReciclajeDto();
		pr.titulo = request.titulo;
		pr.latitud = request.latitud;
		pr.longitud = request.longitud;
		pr.dias = Dia.getDia(request.dias);
		pr.tipoResiduo = request.tiposResiduo != null ? request.tiposResiduo.stream().map(TipoResiduoDto::from).toList() : List.of();
		pr.recicladorId = request.recicladorId;
		return pr;
	}

	public PuntoReciclajeResponse toResponse() {
		return new PuntoReciclajeResponse(
			id,
			titulo,
			latitud,
			longitud,
			dias != null ? dias.stream().map(DiaResponse::from).toList() : null,
			tipoResiduo != null ? tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList() : null,
			reciclador != null ? "/user/" + reciclador.id : "",
			recicladorId,
			reciclador != null ? reciclador.toResponse() : null
		);
	}

	public PuntoReciclaje toEntity() {
		return PuntoReciclaje.builder()
			.id(id)
			.titulo(titulo)
			.latitud(latitud)
			.longitud(longitud)
			.dias(dias)
			.tipoResiduo(tipoResiduo != null ? tipoResiduo.stream().map(TipoResiduoDto::toEntity).toList() : List.of())
			.email(email)
			.recicladorId(recicladorId).build();
	}

	public PuntoVerdeResponse toPuntoVerdeResponse() {
		return new PuntoVerdeResponse(
			id,
			titulo,
			latitud,
			longitud,
			dias.stream().map(DiaResponse::from).toList(),
			tipoResiduo != null ? tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList() : List.of(),
			email
		);
	}
}

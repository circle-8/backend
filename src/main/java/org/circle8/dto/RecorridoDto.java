package org.circle8.dto;


import org.circle8.controller.request.recorrido.PostRecorridoRequest;
import org.circle8.controller.response.RecorridoResponse;
import org.circle8.entity.Recorrido;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public class RecorridoDto {
	public Long id;
	public LocalDate fechaRetiro;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public Long recicladorId;
	public CiudadanoDto reciclador;
	public Long zonaId;
	public Long organizacionId; //requerido para la uri de zona
	public ZonaDto zona;
	public PuntoDto puntoInicio;
	public PuntoDto puntoFin;
	public List<RetiroDto> puntos;

	public static RecorridoDto from(Recorrido entity) {
		if ( entity == null ) return null;
		var r = new RecorridoDto();
		r.id = entity.id;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaInicio = entity.fechaInicio;
		r.fechaFin = entity.fechaFin;
		r.recicladorId = entity.recicladorId;
		r.reciclador = CiudadanoDto.from(entity.reciclador);
		r.zonaId = entity.zonaId;
		r.organizacionId = entity.organizacionId;
		r.zona = ZonaDto.from(entity.zona);
		r.puntoInicio = PuntoDto.from(entity.puntoInicio);
		r.puntoFin = PuntoDto.from(entity.puntoFin);
		r.puntos = entity.puntos != null ? entity.puntos.stream().map(RetiroDto::from).toList() : List.of();
		return r;
	}

	public static RecorridoDto from(PostRecorridoRequest req, long zonaId, long organizacionId) {
		var r = new RecorridoDto();
		r.fechaRetiro = req.fechaRetiro;
		r.recicladorId = req.recicladorId;
		r.zonaId = zonaId;
		r.organizacionId = organizacionId;
		r.puntoInicio = PuntoDto.from(req.puntoInicio);
		r.puntoFin = PuntoDto.from(req.puntoFin);
		return r;
	}

	public Recorrido toEntity() {
		return Recorrido.builder()
			.fechaRetiro(this.fechaRetiro)
			.recicladorId(this.recicladorId)
			.zonaId(this.zonaId)
			.organizacionId(this.organizacionId)
			.puntoInicio(this.puntoInicio.toEntity())
			.puntoFin(this.puntoFin.toEntity())
			.build();
	}

	public RecorridoResponse toResponse() {
		var r = new RecorridoResponse();
		r.id = this.id;
		r.fechaRetiro = this.fechaRetiro;
		r.fechaInicio = this.fechaInicio;
		r.fechaFin = this.fechaFin;
		r.recicladorId = this.recicladorId;
		r.recicladorUri = this.reciclador != null ? "/user/"+this.reciclador.usuarioId : null;
		r.reciclador = this.reciclador != null ? this.reciclador.toResponse() : null;
		r.zonaId = this.zonaId;
		r.zonaUri = (this.zonaId != null && this.organizacionId != null) ?
				"/organizacion/"+this.organizacionId+"/zona/"+this.zonaId : null;
		r.zona = this.zona != null ? this.zona.toResponse() : null;
		r.puntoInicio = this.puntoInicio != null ? this.puntoInicio.toResponse() : null;
		r.puntoFin = this.puntoFin != null ? this.puntoFin.toResponse() : null;
		r.puntos = this.puntos != null ? this.puntos.stream().map(RetiroDto::toResponse).toList() : null;

		return r;
	}
}

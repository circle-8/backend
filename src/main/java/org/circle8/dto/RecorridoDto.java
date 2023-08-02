package org.circle8.dto;


import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.response.RecorridoResponse;
import org.circle8.entity.Recorrido;

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
		r.puntos = this.puntos.stream().map(RetiroDto::toResponse).toList();
		
		return r;
	}
}

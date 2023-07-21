package org.circle8.dto;


import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.response.RecorridoResponse;
import org.circle8.entity.Recorrido;

public class RecorridoDto {
	public Long id;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public Long recicladorId;
	public CiudadanoDto reciclador;
	public Long zonaId;
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
		r.zona = ZonaDto.from(entity.zona);
		r.puntoInicio = PuntoDto.from(entity.puntoInicio);
		r.puntoFin = PuntoDto.from(entity.puntoFin);
		r.puntos = entity.puntos.stream().map(RetiroDto::from).toList();
		return r;
	}

	public RecorridoResponse toResponse() {
		return new RecorridoResponse();
	}
}

package org.circle8.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.circle8.entity.Recorrido;

public class RecorridoDto {
	public Long id;
	public LocalDateTime fechaRetiro;
	public LocalDateTime fechaInicio;
	public LocalDateTime fechaFin;
	public String recicladorUri;
	public Long recicladorId;
	public CiudadanoDto reciclador;
	public String zonaUri;
	public Long zonaId;
	public ZonaDto zona;
	public PuntoDto puntoInicio;
	public PuntoDto puntoFin;
	public List<RetiroDto> puntos;
	
	public static RecorridoDto from(Recorrido entity) {
		var r = new RecorridoDto();
		r.id = entity.id;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaInicio = entity.fechaInicio;
		r.fechaFin = entity.fechaFin;
		r.recicladorUri = entity.recicladorUri;
		r.recicladorId = entity.recicladorId;
		r.reciclador = CiudadanoDto.from(entity.reciclador);
		r.zonaUri = entity.zonaUri;
		r.zonaId = entity.zonaId;
		r.zona = ZonaDto.from(entity.zona);
		r.puntoInicio = PuntoDto.from(entity.puntoInicio);
		r.puntoFin = PuntoDto.from(entity.puntoFin);
		r.puntos = entity.puntos.stream().map(RetiroDto::from).toList();
		return r;
	}
}

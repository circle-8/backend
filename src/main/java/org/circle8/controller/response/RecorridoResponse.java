package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RecorridoResponse implements ApiResponse {
	public long id;
	public LocalDate fechaRetiro;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public String recicladorUri;
	public Long recicladorId;
	public CiudadanoResponse reciclador;
	public String zonaUri;
	public Long zonaId;
	public ZonaResponse zona;
	public PuntoResponse puntoInicio;
	public PuntoResponse puntoFin;
	public List<RetiroResponse> puntos;
}

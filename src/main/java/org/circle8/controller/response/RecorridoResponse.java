package org.circle8.controller.response;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RecorridoResponse implements ApiResponse {
	public int id;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public String recicladorUri;
	public Integer recicladorId;
	public CiudadanoResponse reciclador;
	public String zonaUri;
	public Integer zonaId;
	public ZonaResponse zona;
	public PuntoResponse puntoInicio;
	public PuntoResponse puntoFin;
	public List<RetiroResponse> puntos;
}

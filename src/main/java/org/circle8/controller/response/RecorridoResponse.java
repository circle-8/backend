package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RecorridoResponse implements ApiResponse {
	public int id;
	public LocalDateTime fechaRetiro;
	public LocalDateTime fechaInicio;
	public LocalDateTime fechaFin;
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

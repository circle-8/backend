package org.circle8.response;

import java.time.LocalDateTime;
import java.util.List;

public class RecorridoResponse implements ApiResponse {
	public int id;
	public LocalDateTime fechaRetiro;
	public LocalDateTime fechaInicio;
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

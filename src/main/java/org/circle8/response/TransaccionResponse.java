package org.circle8.response;

import java.time.LocalDateTime;
import java.util.List;

public class TransaccionResponse implements ApiResponse {
	public int id;
	public LocalDateTime fechaCreacion;
	public LocalDateTime fechaRetiro;
	public String transporteUri;
	public Integer transporteId;
	public TransporteResponse transporte;
	public String puntoReciclajeUri;
	public Integer puntoReciclajeId;
	public PuntoReciclajeResponse puntoReciclaje;
	public List<ResiduoResponse> residuos;
}

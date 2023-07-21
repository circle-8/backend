package org.circle8.controller.response;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransaccionResponse implements ApiResponse {
	public int id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;
	public String transporteUri;
	public Integer transporteId;
	public TransporteResponse transporte;
	public String puntoReciclajeUri;
	public Integer puntoReciclajeId;
	public PuntoReciclajeResponse puntoReciclaje;
	public List<ResiduoResponse> residuos;
}

package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransaccionResponse implements ApiResponse {
	public Long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;
	public String transporteUri;
	public Long transporteId;
	public TransporteResponse transporte;
	public String puntoReciclajeUri;
	public Long puntoReciclajeId;
	public PuntoReciclajeResponse puntoReciclaje;
	public List<ResiduoResponse> residuos;
}

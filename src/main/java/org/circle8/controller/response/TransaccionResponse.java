package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

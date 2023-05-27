package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class ResiduoResponse implements ApiResponse {
	public int id;
	public LocalDateTime fechaRetiro;
	public LocalDateTime fechaCreacion;
	public String puntoResiduoUri;
	public Integer puntoResiduoId;
	public PuntoResiduoResponse puntoResiduo;
	public TipoResiduoResponse tipoResiduo;
	public String recorridoUri;
	public Integer recorridoId;
	public RecorridoResponse recorrido;
	public String transaccionUri;
	public Integer transaccionId;
	public TransaccionResponse transaccion;
}

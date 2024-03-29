package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ResiduoResponse implements ApiResponse {
	public long id;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaLimiteRetiro;
	public String descripcion;
	public String puntoResiduoUri;
	public Long puntoResiduoId;
	public PuntoResiduoResponse puntoResiduo;
	public TipoResiduoResponse tipoResiduo;
	public String recorridoUri;
	public Long recorridoId;
	public String transaccionUri;
	public Long transaccionId;
	public TransaccionResponse transaccion;
	public String base64;
}

package org.circle8.controller.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransporteResponse implements ApiResponse {
	public int id;
	public ZonedDateTime fechaAcordada;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public BigDecimal precioAcordado;
	public String transportistaUri;
	public Integer transportistaId;
	public TransportistaResponse transportista;
	public String transaccionUri;
	public Integer transaccionId;
	public boolean pagoConfirmado;
	public boolean entregaConfirmada;

	// No puede tener TransaccionResponse, pues no se puede serializar
	// un JSON recursivo
}

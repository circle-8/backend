package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransporteResponse implements ApiResponse {
	public Long id;
	public LocalDate fechaAcordada;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public BigDecimal precioAcordado;
	public String transportistaUri;
	public Long transportistaId;
	public TransportistaResponse transportista;
	public String transaccionUri;
	public Long transaccionId;
	public boolean pagoConfirmado;
	public boolean entregaConfirmada;
	public BigDecimal precioSugerido;

	// No puede tener TransaccionResponse, pues no se puede serializar
	// un JSON recursivo
}

package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransporteResponse implements ApiResponse {
	public int id;
	public LocalDateTime fechaAcordada;
	public LocalDateTime fechaInicio;
	public LocalDateTime fechaFin;
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

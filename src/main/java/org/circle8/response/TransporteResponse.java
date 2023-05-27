package org.circle8.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransporteResponse implements ApiResponse {
	public int id;
	public LocalDateTime fecha;
	public BigDecimal precioAcordado;
	public String transportistaUri;
	public Integer transportistaId;
	public TransportistaResponse transportista;
	public String transaccionUri;
	public Integer transaccionId;

	// No puede tener TransaccionResponse, pues no se puede serializar
	// un JSON recursivo
}

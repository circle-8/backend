package org.circle8.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transporte {
	public Long id;
	public LocalDateTime fechaAcordada;
	public LocalDateTime fechaInicio;
	public LocalDateTime fechaFin;
	public BigDecimal precioAcordado;
	public String transportistaUri;
	public Long transportistaId;
	public Transportista transportista;
	public String transaccionUri;
	public Long transaccionId;
	public boolean pagoConfirmado;
	public boolean entregaConfirmada;
}

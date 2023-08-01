package org.circle8.entity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Transporte {
	public Long id;
	public ZonedDateTime fechaAcordada;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public BigDecimal precioAcordado;
	public Long transportistaId;
	public Transportista transportista;
	public Long transaccionId;
	public boolean pagoConfirmado;
	public boolean entregaConfirmada;
}

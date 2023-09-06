package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transporte {
	public Long id;
	public LocalDate fechaAcordada;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public BigDecimal precioAcordado;
	public Long transportistaId;
	public Transportista transportista;
	public Long transaccionId;
	public Boolean pagoConfirmado;
	public Boolean entregaConfirmada;
	public BigDecimal precioSugerido;
}

package org.circle8.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.circle8.entity.Transporte;

public class TransporteDto {
	public Long id;
	public LocalDateTime fechaAcordada;
	public LocalDateTime fechaInicio;
	public LocalDateTime fechaFin;
	public BigDecimal precioAcordado;
	public Long transportistaId;
	public TransportistaDto transportista;
	public Long transaccionId;
	public boolean pagoConfirmado;
	public boolean entregaConfirmada;
	
	public static TransporteDto from(Transporte entity) {
		var t = new TransporteDto();
		t.id = entity.id;
		t.fechaAcordada = entity.fechaAcordada;
		t.fechaInicio = entity.fechaInicio;
		t.fechaFin = entity.fechaFin;
		t.precioAcordado = entity.precioAcordado;
		t.transportistaId = entity.transportistaId;
		t.transportista = TransportistaDto.from(entity.transportista);
		t.transaccionId = entity.transaccionId;
		t.pagoConfirmado = entity.pagoConfirmado;
		t.entregaConfirmada = entity.entregaConfirmada;
		return t;
	}
}

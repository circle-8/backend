package org.circle8.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.request.transporte.TransportePutRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTransporte {
	public long id;
	public Boolean pagoConfirmado;
	public Boolean entregaConfirmada;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public LocalDate fechaAcordada;
	public BigDecimal precioAcordado;
	public Long transportistaId;

	public static UpdateTransporte from(long id, TransportePutRequest req) {
		return UpdateTransporte.builder()
			.id(id)
			.precioAcordado(req.precioAcordado)
			.fechaAcordada(req.fechaAcordada)
			.transportistaId(req.transportistaId)
			.build();
	}
}

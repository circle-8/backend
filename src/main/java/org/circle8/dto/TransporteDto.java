package org.circle8.dto;

import org.circle8.controller.request.transporte.TransportePutRequest;
import org.circle8.controller.response.TransporteResponse;
import org.circle8.entity.Transporte;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class TransporteDto {
	public Long id;
	public LocalDate fechaAcordada;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public BigDecimal precioAcordado;
	public Long transportistaId;
	public TransportistaDto transportista;
	public Long transaccionId;
	public TransaccionDto transaccion;
	public Boolean pagoConfirmado;
	public Boolean entregaConfirmada;
	public BigDecimal precioSugerido;

	public static TransporteDto from(TransportePutRequest req) {
		var t = new TransporteDto();
		t.fechaAcordada = req.fechaAcordada;
		t.precioAcordado = req.precioAcordado;
		t.transaccionId = req.transportistaId;
		return t;
	}

	public static TransporteDto from(Transporte entity) {
		if ( entity == null ) return null;
		var t = new TransporteDto();
		t.id = entity.id;
		t.fechaAcordada = entity.fechaAcordada;
		t.fechaInicio = entity.fechaInicio;
		t.fechaFin = entity.fechaFin;
		t.precioAcordado = entity.precioAcordado;
		t.transportistaId = entity.transportistaId;
		t.transportista = entity.transportista != null ?
				TransportistaDto.from(entity.transportista) : null;
		t.transaccionId = entity.transaccionId;
		t.transaccion = entity.transaccion != null ?
				TransaccionDto.from(entity.transaccion) : null;
		t.pagoConfirmado = entity.pagoConfirmado;
		t.entregaConfirmada = entity.entregaConfirmada;
		t.precioSugerido = entity.precioSugerido;
		return t;
	}

	public TransporteResponse toResponse() {
		return new TransporteResponse(
			id,
			fechaAcordada,
			fechaInicio,
			fechaFin,
			precioAcordado != null ? precioAcordado.longValue() : null,
			transportista != null ? "/user/" + transportista.usuarioId : null,
			transportistaId,
			transportista != null ? transportista.toResponse() : null,
			transaccionId != null ? "/transaccion/" + transaccionId : null,
			transaccionId,
			transaccion != null ? transaccion.toResponse() : null,
			pagoConfirmado,
			entregaConfirmada,
			precioSugerido != null ? precioSugerido.longValue() : null
		);
	}

	public Transporte toEntity() {
		var t = new Transporte();
		t.id = id;
		t.fechaAcordada = fechaAcordada;
		t.fechaInicio = fechaInicio;
		t.fechaFin = fechaFin;
		t.precioAcordado = precioAcordado;
		t.transportistaId = transportistaId;
		t.transaccionId = transaccionId;
		t.pagoConfirmado = pagoConfirmado;
		t.entregaConfirmada = entregaConfirmada;
		t.precioSugerido = precioSugerido;
		return t;
	}
}

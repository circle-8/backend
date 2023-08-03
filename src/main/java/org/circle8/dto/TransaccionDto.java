package org.circle8.dto;

import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.response.TransaccionResponse;
import org.circle8.entity.Transaccion;

public class TransaccionDto {
	public Long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;
	public Long transporteId;
	public TransporteDto transporte;
	public String puntoReciclajeUri;
	public Long puntoReciclajeId;
	public PuntoReciclajeDto puntoReciclaje;
	public List<ResiduoDto> residuos;

	public static TransaccionDto from(Transaccion entity) {
		if ( entity == null || entity.id == 0 ) return null;
		var t = new TransaccionDto();
		t.id = entity.id;
		t.fechaCreacion = entity.fechaCreacion;
		t.fechaRetiro = entity.fechaRetiro;
		t.transporteId = entity.transporteId;
		t.transporte = TransporteDto.from(entity.transporte);
		t.puntoReciclajeId = entity.puntoReciclajeId;
		t.puntoReciclaje = PuntoReciclajeDto.from(entity.puntoReciclaje);
		t.residuos = entity.residuos.stream().map(ResiduoDto::from).toList();
		return t;
	}

	public TransaccionResponse toResponse(){
		return new TransaccionResponse(id,
			fechaCreacion,
			fechaRetiro,
			transporte != null ? "/transporte/" + transporteId: null,
			transporte == null ? transporteId : null,
			transporte != null ? transporte.toResponse() : null,
			puntoReciclaje != null ? "/reciclador/" + puntoReciclaje.recicladorId + "/punto_reciclaje/" + puntoReciclaje.id : null,
			puntoReciclaje == null ? puntoReciclajeId : null,
			puntoReciclaje != null ? puntoReciclaje.toResponse() : null,
			residuos != null ? residuos.stream().map(ResiduoDto::toResponse).toList() : null);
	}
}

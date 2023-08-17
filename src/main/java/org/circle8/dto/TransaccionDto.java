package org.circle8.dto;

import lombok.val;
import org.circle8.controller.request.transaccion.TransaccionPostRequest;
import org.circle8.controller.request.transaccion.TransaccionPutRequest;
import org.circle8.controller.response.TransaccionResponse;
import org.circle8.entity.Transaccion;

import java.time.ZonedDateTime;
import java.util.List;

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

	public Transaccion toEntity() {
		Transaccion t = new Transaccion();
		t.id = id != null ? id : null;
		t.fechaCreacion = fechaCreacion;
		t.fechaRetiro = fechaRetiro;
		t.transporteId = transporteId;
		t.transporte = transporte != null ? transporte.toEntity() : null;
		t.puntoReciclaje = puntoReciclaje != null ? puntoReciclaje.toEntity() : null;
		t.puntoReciclajeId = puntoReciclajeId;
		t.residuos = residuos != null ? residuos.stream().map(ResiduoDto::toEntity).toList() : null;
		return t;
	}

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
		t.residuos = entity.residuos != null ? entity.residuos.stream().map(ResiduoDto::from).toList() : List.of();
		return t;
	}

	public static TransaccionDto from(TransaccionPutRequest req) {
		val t = new TransaccionDto();
		t.id = req.id;
		t.fechaRetiro = req.fechaRetiro;
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
			(residuos != null && !residuos.isEmpty()) ? residuos.stream().map(ResiduoDto::toResponse).toList() : null);
	}

	public static TransaccionDto from(TransaccionPostRequest req) {
		if ( req == null) return null;
		var t = new TransaccionDto();
		t.residuos = req.residuosId.stream()
											.map(id -> {
												ResiduoDto residuoDto = new ResiduoDto();
												residuoDto.id =id;
												return residuoDto;
											})
											.toList();
		t.puntoReciclajeId = req.puntoReciclajeId;
		return t;
	}
}

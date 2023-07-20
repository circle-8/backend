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
		if ( entity == null ) return null;
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
		//TODO: llenar cuando corresponda
		return new TransaccionResponse();
	}
}

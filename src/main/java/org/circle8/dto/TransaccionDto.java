package org.circle8.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.circle8.entity.Transaccion;

public class TransaccionDto {
	public Long id;
	public LocalDateTime fechaCreacion;
	public LocalDateTime fechaRetiro;
	public String transporteUri;
	public Long transporteId;
	public TransporteDto transporte;
	public String puntoReciclajeUri;
	public Long puntoReciclajeId;
	public PuntoReciclajeDto puntoReciclaje;
	public List<ResiduoDto> residuos;
	
	public static TransaccionDto from(Transaccion entity) {
		var t = new TransaccionDto();
		t.id = entity.id;
		t.fechaCreacion = entity.fechaCreacion;
		t.fechaRetiro = entity.fechaRetiro;
		t.transporteUri = entity.transporteUri;
		t.transporteId = entity.transporteId;
		t.transporte = TransporteDto.from(entity.transporte);
		t.puntoReciclajeUri = entity.puntoReciclajeUri;
		t.puntoReciclajeId = entity.puntoReciclajeId;
		t.puntoReciclaje = PuntoReciclajeDto.from(entity.puntoReciclaje);
		t.residuos = entity.residuos.stream().map(ResiduoDto::from).toList();
		return t;
	}
}

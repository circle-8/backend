package org.circle8.dto;

import java.time.LocalDateTime;

import org.circle8.controller.request.residuo.ResiduoRequest;
import org.circle8.entity.Residuo;

public class ResiduoDto {
	public long id;
	public LocalDateTime fechaCreacion;
	public LocalDateTime fechaRetiro;	
	public LocalDateTime fechaLimiteRetiro;
	public String descripcion;
	public String puntoResiduoUri;
	public Long puntoResiduoId;
	public PuntoResiduoDto puntoResiduo;
	public String tipoResiduoUri;
	public Long tipoResiduoId;
	public TipoResiduoDto tipoResiduo;
	public String recorridoUri;
	public Long recorridoId;
	public RecorridoDto recorrido;
	public String transaccionUri;
	public Long transaccionId;
	public TransaccionDto transaccion;
	
	public static ResiduoDto from(ResiduoRequest req) {
		var r = new ResiduoDto();
		r.fechaLimiteRetiro = req.fechaLimite;
		r.descripcion = req.descripcion;
		r.puntoResiduoId = req.puntoResiduo;
		r.tipoResiduoId = req.tipoResiduoId;
		r.recorridoId = req.recorridoId;
		r.transaccionId = req.transaccionId;
		return r;
	}
	
	public static ResiduoDto from(Residuo entity) {
		var r = new ResiduoDto();
		r.id = entity.id;
		r.fechaCreacion = entity.fechaCreacion;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaLimiteRetiro = entity.fechaLimiteRetiro;
		r.descripcion = entity.descripcion;
		r.puntoResiduoUri = entity.puntoResiduoUri;
		r.puntoResiduoId = entity.puntoResiduoId;
		r.puntoResiduo = PuntoResiduoDto.from(entity.puntoResiduo);
		r.tipoResiduoUri = r.tipoResiduoUri;
		r.tipoResiduoId = r.tipoResiduoId;
		r.tipoResiduo = TipoResiduoDto.from(entity.tipoResiduo);
		r.recorridoUri = entity.recorridoUri;
		r.recorridoId = entity.recorridoId;
		r.recorrido = RecorridoDto.from(entity.recorrido);
		r.transaccionUri = entity.transaccionUri;
		r.transaccionId = entity.transaccionId;
		r.transaccion = TransaccionDto.from(entity.transaccion);
		return r;
	}
}

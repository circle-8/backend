package org.circle8.dto;

import java.time.LocalDateTime;

import org.circle8.controller.request.residuo.ResiduoRequest;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.entity.Residuo;

public class ResiduoDto {
	public long id;
	public LocalDateTime fechaCreacion;
	public LocalDateTime fechaRetiro;	
	public LocalDateTime fechaLimiteRetiro;
	public String descripcion;
	public Long ciudadanoId;
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
		r.ciudadanoId = req.ciudadnoId;
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
	
	public ResiduoResponse toResponse() {
		return new ResiduoResponse(
				this.id,
				this.fechaRetiro,
				this.fechaCreacion,
				this.fechaLimiteRetiro,
				this.descripcion,
				"/ciudadano/"+this.ciudadanoId+"/punto_residuo/"+this.puntoResiduoId,
				this.puntoResiduoId,
				this.puntoResiduo != null ? this.puntoResiduo.toResponse() : null,
				"/tipo_residuo/"+this.tipoResiduoId,
				this.tipoResiduoId,
				this.tipoResiduo != null ? this.tipoResiduo.toResponse() : null,
				this.recorridoId != null ? "/recorrido/"+this.recorridoId : null,
				this.recorridoId,
				this.recorrido != null ? this.recorrido.toResponse() : null,
				this.transaccionId != null ? "/transaccion/"+this.transaccionId : null,
				this.transaccionId,
				this.transaccion != null ? this.transaccion.toResponse() : null);
	}
	
	public Residuo toEntity() {	
		//Son los atributos necesarios para hacer el POST
		return Residuo.builder()				
				.fechaLimiteRetiro(this.fechaLimiteRetiro)
				.descripcion(this.descripcion)
				.puntoResiduoId(this.puntoResiduoId)
				.tipoResiduoId(this.tipoResiduoId)
				.build();
	}	
}

package org.circle8.dto;

import java.time.ZonedDateTime;

import org.circle8.controller.request.residuo.PostResiduoRequest;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;

public class ResiduoDto {
	public long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;	
	public ZonedDateTime fechaLimiteRetiro;
	public String descripcion;
	public Long ciudadanoId;
	public Long puntoResiduoId;
	public PuntoResiduoDto puntoResiduo;
	public Long tipoResiduoId;
	public TipoResiduoDto tipoResiduo;
	public Long recorridoId;
	public RecorridoDto recorrido;
	public Long transaccionId;
	public TransaccionDto transaccion;
	
	public static ResiduoDto from(PostResiduoRequest req) {
		var r = new ResiduoDto();
		r.fechaLimiteRetiro = req.fechaLimite;
		r.descripcion = req.descripcion;
		r.ciudadanoId = req.ciudadanoId;
		r.puntoResiduoId = req.puntoResiduoId;
		var pr = PuntoResiduo.builder()
				.id(req.puntoResiduoId)
				.ciudadano(User.builder().id(req.ciudadanoId).build())
				.build();
		r.puntoResiduo = PuntoResiduoDto.from(pr);
		r.tipoResiduoId = req.tipoResiduoId;
		r.tipoResiduo = TipoResiduoDto.from(TipoResiduo.builder().id(req.tipoResiduoId).build());
		return r;
	}
	
	public static ResiduoDto from(Residuo entity) {
		var r = new ResiduoDto();
		r.id = entity.id;
		r.fechaCreacion = entity.fechaCreacion;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaLimiteRetiro = entity.fechaLimiteRetiro;
		r.descripcion = entity.descripcion;
		r.puntoResiduo = PuntoResiduoDto.from(entity.puntoResiduo);
		r.tipoResiduoId = r.tipoResiduoId;
		r.tipoResiduo = TipoResiduoDto.from(entity.tipoResiduo);
		r.recorrido = RecorridoDto.from(entity.recorrido);
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
				.puntoResiduo(this.puntoResiduo.toEntity())
				.tipoResiduo(this.tipoResiduo.toEntity())
				.build();
	}	
}

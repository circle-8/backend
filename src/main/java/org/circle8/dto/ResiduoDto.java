package org.circle8.dto;


import java.time.ZonedDateTime;
import java.util.List;

import org.circle8.controller.request.residuo.PostResiduoRequest;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.entity.Residuo;

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
	public Long transaccionId;
	public TransaccionDto transaccion;

	public static ResiduoDto from(PostResiduoRequest req) {
		var r = new ResiduoDto();
		r.fechaLimiteRetiro = req.fechaLimite;
		r.descripcion = req.descripcion;
		r.ciudadanoId = req.ciudadanoId;
		r.puntoResiduoId = req.puntoResiduoId;
		r.puntoResiduo = PuntoResiduoDto.builder()
				.id(req.puntoResiduoId)
				.ciudadanoId(req.ciudadanoId)
				.ciudadano(UserDto.builder().id(req.ciudadanoId).build())
				.residuos(List.of())
				.build();
		r.tipoResiduoId = req.tipoResiduoId;
		r.tipoResiduo = TipoResiduoDto.builder().id(req.tipoResiduoId).build();
		return r;
	}

	public static ResiduoDto from(Residuo entity) {
		if ( entity == null ) return null;
		var r = new ResiduoDto();
		r.id = entity.id;
		r.ciudadanoId = entity.ciudadanoId;
		r.fechaCreacion = entity.fechaCreacion;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaLimiteRetiro = entity.fechaLimiteRetiro;
		r.descripcion = entity.descripcion;
		r.puntoResiduo = PuntoResiduoDto.from(entity.puntoResiduo);
		r.tipoResiduo = TipoResiduoDto.from(entity.tipoResiduo);
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
				this.puntoResiduo != null ? "/ciudadano/"+this.ciudadanoId+"/punto_residuo/"+this.puntoResiduo.id : null,
				this.puntoResiduo != null ? this.puntoResiduo.id: null,
				this.puntoResiduo != null ? this.puntoResiduo.toResponse(): null,
				this.tipoResiduo != null ? this.tipoResiduo.toResponse() : null,
				this.recorridoId != null ? "/recorrido/"+this.recorridoId : null,
				this.recorridoId,
				this.transaccion != null ? "/transaccion/"+this.transaccion.id : null,
				this.transaccion != null ? this.transaccion.id : null,
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

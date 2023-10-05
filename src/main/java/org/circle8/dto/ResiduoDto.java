package org.circle8.dto;


import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.request.residuo.PostResiduoRequest;
import org.circle8.controller.request.residuo.PutResiduoRequest;
import org.circle8.controller.response.ResiduoResponse;
import org.circle8.entity.Residuo;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	public byte[] base64;

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
		r.base64 = !Strings.isNullOrEmpty(req.base64) ? Base64.getDecoder().decode(req.base64) : null;
		r.tipoResiduoId = req.tipoResiduoId;
		r.tipoResiduo = TipoResiduoDto.builder().id(req.tipoResiduoId).build();
		return r;
	}

	public static ResiduoDto from(PutResiduoRequest req) {
		return ResiduoDto.builder()
			.id(req.id)
			.tipoResiduo(TipoResiduoDto.builder().id(req.tipoResiduoId).build())
			.fechaLimiteRetiro(req.fechaLimite)
			.descripcion(req.descripcion)
			.base64(!Strings.isNullOrEmpty(req.base64) ? Base64.getDecoder().decode(req.base64) : null)
			.build();
	}

	public static ResiduoDto from(Residuo entity) {
		if ( entity == null ) return null;
		var r = new ResiduoDto();
		r.id = entity.id;
		r.ciudadanoId = entity.ciudadano != null ? entity.ciudadano.id : null;
		r.fechaCreacion = entity.fechaCreacion;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaLimiteRetiro = entity.fechaLimiteRetiro;
		r.descripcion = entity.descripcion;
		r.puntoResiduo = PuntoResiduoDto.from(entity.puntoResiduo);
		r.base64 = entity.base64;
		r.tipoResiduo = TipoResiduoDto.from(entity.tipoResiduo);
		r.transaccion = TransaccionDto.from(entity.transaccion);
		r.recorridoId = entity.recorrido != null ? entity.recorrido.id : null;
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
			this.recorridoId != null && !recorridoId.equals(0L) ? "/recorrido/"+this.recorridoId : null,
			this.recorridoId != null && !recorridoId.equals(0L) ? this.recorridoId : null,
			this.transaccion != null ? "/transaccion/"+this.transaccion.id : null,
			this.transaccion != null ? this.transaccion.id : null,
			this.transaccion != null ? this.transaccion.toResponse() : null,
			this.base64 != null ? Base64.getEncoder().encodeToString(this.base64) : null
		);
	}

	public Residuo toEntity() {
		//Son los atributos necesarios para hacer el POST
		return Residuo.builder()
			.id(this.id)
			.fechaLimiteRetiro(this.fechaLimiteRetiro)
			.descripcion(this.descripcion)
			.puntoResiduo(this.puntoResiduo != null ? this.puntoResiduo.toEntity() : null)
			.tipoResiduo(this.tipoResiduo != null ? this.tipoResiduo.toEntity() : null)
			.base64(this.base64)
			.build();
	}
}

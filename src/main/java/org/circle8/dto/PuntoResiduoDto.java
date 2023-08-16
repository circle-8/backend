package org.circle8.dto;

import java.util.List;

import org.circle8.controller.request.punto_residuo.PostPutPuntoResiduoRequest;
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.entity.PuntoResiduo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PuntoResiduoDto {
	public long id;
	public Double latitud;
	public Double longitud;
	public Long ciudadanoId;
	public UserDto ciudadano;
	public List<ResiduoDto> residuos;

	public static PuntoResiduoDto from(PostPutPuntoResiduoRequest request) {
		var pr = new PuntoResiduoDto();
		var user = new UserDto();
		user.id = request.ciudadanoId;
		pr.id = request.id;
		pr.latitud = request.latitud;
		pr.longitud = request.longitud;
		pr.ciudadanoId = request.ciudadanoId;
		pr.ciudadano = user;
		pr.residuos = List.of();
		return pr;
	}

	public static PuntoResiduoDto from(PuntoResiduo entity) {
		if ( entity == null ) return null;
		val p = new PuntoResiduoDto();
		p.id = entity.id;
		p.latitud = entity.latitud;
		p.longitud = entity.longitud;
		p.ciudadanoId = entity.ciudadanoId;
		p.ciudadano = UserDto.from(entity.ciudadano);
		p.residuos = entity.residuos != null ? entity.residuos.stream().map(ResiduoDto::from).toList() : List.of();
		return p;
	}

	public PuntoResiduoResponse toResponse() {
		val r = new PuntoResiduoResponse();
		r.id = this.id;
		r.latitud = this.latitud;
		r.longitud = this.longitud;
		r.ciudadanoId = this.ciudadanoId;
		r.ciudadanoUri = this.ciudadano != null ? "/user/" + this.ciudadano.id : null;
		r.ciudadano = this.ciudadano != null ? this.ciudadano.toResponse() : null;
		r.residuos = !residuos.isEmpty() ? residuos.stream().map(ResiduoDto::toResponse).toList() : null;
		return r;
	}

	public PuntoResiduo toEntity() {
		return PuntoResiduo.builder()
				.id(this.id)
				.latitud(this.latitud)
				.longitud(this.longitud)
				.ciudadanoId(this.ciudadanoId)
				.residuos(List.of())
				.build();
	}
}

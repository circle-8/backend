package org.circle8.dto;

import lombok.val;
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.entity.PuntoResiduo;

import java.util.List;

public class PuntoResiduoDto {
	public long id;
	public Double latitud;
	public Double longitud;
	public Long ciudadanoId;
	public UserDto ciudadano;
	public List<ResiduoDto> residuos;

	public static PuntoResiduoDto from(PuntoResiduo entity) {
		if ( entity == null ) return null;
		val p = new PuntoResiduoDto();
		p.id = entity.id;
		p.latitud = entity.latitud;
		p.longitud = entity.longitud;
		p.ciudadanoId = entity.ciudadanoId;
		p.ciudadano = UserDto.from(entity.ciudadano);
		p.residuos = entity.residuos.stream().map(ResiduoDto::from).toList();
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
}

package org.circle8.dto;

import org.circle8.controller.response.CiudadanoResponse;
import org.circle8.entity.Ciudadano;

public class CiudadanoDto {
	public long id;
	public String username;
	public String nombre;
	public long usuarioId;

	public static CiudadanoDto from(Ciudadano entity) {
		if ( entity == null ) return null;
		var c = new CiudadanoDto();
		c.id = entity.id;
		c.username = entity.username;
		c.nombre = entity.nombre;
		c.usuarioId = entity.usuarioId;
		return c;
	}

	public CiudadanoResponse toResponse() {
		return CiudadanoResponse.builder()
				.id(this.id)
				.username(this.username)
				.nombre(this.nombre)
				.build();
	}
}

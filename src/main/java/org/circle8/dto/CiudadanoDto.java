package org.circle8.dto;

import org.circle8.controller.response.CiudadanoResponse;
import org.circle8.entity.Ciudadano;

public class CiudadanoDto {
	public int id;
	public String username;
	public String nombre;
	
	public static CiudadanoDto from(Ciudadano entity){
		var c = new CiudadanoDto();
		c.id = entity.id;
		c.username = entity.username;
		c.nombre = entity.nombre;
		return c;
	}
	
	public CiudadanoResponse toResponse() {
		return new CiudadanoResponse(id, username, nombre);
	}
	
	public Ciudadano toEntity() {
		return Ciudadano.builder()
				.id(id)
				.username(username)
				.nombre(nombre)
				.build();
	}
}

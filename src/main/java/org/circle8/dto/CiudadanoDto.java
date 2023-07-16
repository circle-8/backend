package org.circle8.dto;

import org.circle8.entity.Ciudadano;

public class CiudadanoDto {
	public long id;
	public String username;
	public String nombre;
	public long usuarioId;
	
	public static CiudadanoDto from(Ciudadano entity) {
		var c = new CiudadanoDto();
		c.id = entity.id;
		c.username = entity.username;
		c.nombre = entity.nombre;
		c.usuarioId = entity.usuarioId;
		return c;
	}
}

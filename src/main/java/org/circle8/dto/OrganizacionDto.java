package org.circle8.dto;

import org.circle8.controller.response.OrganizacionResponse;
import org.circle8.entity.Organizacion;

public class OrganizacionDto {
	public long id;
	public String nombre;
	public String razonSocial;
	
	public static OrganizacionDto from(Organizacion entity) {
		if ( entity == null ) return null;
		var o = new OrganizacionDto();
		o.id = entity.id;
		o.nombre = entity.nombre;
		o.razonSocial = entity.razonSocial;
		return o;
	}
	
	public OrganizacionResponse toResponse() {
		return OrganizacionResponse.builder()
				.id(this.id)
				.nombre(this.nombre)
				.razonSocial(this.razonSocial)
				.build();
	}
	
}

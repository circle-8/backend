package org.circle8.dto;

import org.circle8.controller.response.OrganizacionResponse;
import org.circle8.entity.Organizacion;

public class OrganizacionDto {
	public long id;
	public String razonSocial;
	public Long usuarioId;
	
	public static OrganizacionDto from(Organizacion entity) {
		if ( entity == null ) return null;
		var o = new OrganizacionDto();
		o.id = entity.id;
		o.razonSocial = entity.razonSocial;
		o.usuarioId = entity.usuarioId;
		return o;
	}
	
	public OrganizacionResponse toResponse() {
		var org = new OrganizacionResponse();
		org.id = this.id;
		org.razonSocial = this.razonSocial;
		org.usuarioId = this.usuarioId;
		org.usuarioUri = this.usuarioId != null ? "/user/"+this.usuarioId : null;		
		return org;
	}
	
}

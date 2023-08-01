package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrganizacionResponse implements ApiResponse {
	public long id;
	public String nombre;
	public String razonSocial;
	public String usuarioUri;
	public Long usuarioId;
	
}

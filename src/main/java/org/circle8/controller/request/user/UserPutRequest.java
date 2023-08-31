package org.circle8.controller.request.user;

import org.circle8.controller.request.IRequest;
import org.circle8.controller.response.TipoUsuarioResponse;

import com.google.common.base.Strings;

import lombok.ToString;

@ToString
public class UserPutRequest implements IRequest {
	
	public String username;
	public String nombre;
	public String email;
	public TipoUsuarioResponse tipoUsuario;
	public String razonSocial;
	public Long zonaId;
	public Long organizacionId;

	@Override
	public Validation valid() {
		final var validation = new Validation();
		if ( Strings.isNullOrEmpty(username)
				&& Strings.isNullOrEmpty(nombre)
				&& Strings.isNullOrEmpty(email)
				&& tipoUsuario == null
				&& Strings.isNullOrEmpty(razonSocial)
				&& zonaId == null
				&& organizacionId == null )
			validation.add("Debe enviar al menos un campo para actualizar");
	
		return validation;
	}
}

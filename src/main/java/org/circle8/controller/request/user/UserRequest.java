package org.circle8.controller.request.user;

import com.google.common.base.Strings;
import org.circle8.controller.response.TipoUsuarioResponse;

public class UserRequest implements IRequest {
	/* Obligatorios */
	public String username;
	public String password;
	public String nombre;
	public TipoUsuarioResponse tipoUsuario;

	/* Opcionales */
	public String razonSocial;
	public Integer zonaId;
	public Integer organizacionId;

	@Override
	public Validation valid() {
		final var validation = new Validation();
		if ( Strings.isNullOrEmpty(username) )
			validation.add("falta 'username'");
		if ( Strings.isNullOrEmpty(password) )
			validation.add("falta 'password'");
		if ( Strings.isNullOrEmpty(nombre) )
			validation.add("falta 'nombre'");
		if ( tipoUsuario == null )
			validation.add("falta 'tipoUsuario'");

		return validation;
	}
}

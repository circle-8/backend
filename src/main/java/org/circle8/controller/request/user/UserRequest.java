package org.circle8.controller.request.user;

import com.google.common.base.Strings;
import lombok.ToString;
import org.circle8.controller.response.TipoUsuarioResponse;

import java.util.Arrays;

@ToString
public class UserRequest implements IRequest {
	/* Obligatorios */
	public String username;
	public String password;
	public String nombre;
	public String email;
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
		if ( Strings.isNullOrEmpty(email) )
			validation.add("falta 'email'");
		if ( tipoUsuario == null )
			validation.add(String.format("'tipoUsuario' debe ser uno de %s", Arrays.toString(TipoUsuarioResponse.values())));

		// TODO: si tiene razonSocial, tipo de usuario debe ser organizacion, y viceversa
		// TODO: si tiene zona, tipo de usuario debe ser reciclador urbano, y viceversa
		// TODO: si tiene organizacionId, tipo de usuario debe ser reciclador urbano, y viceversa

		return validation;
	}
}

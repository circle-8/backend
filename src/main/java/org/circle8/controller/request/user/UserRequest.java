package org.circle8.controller.request.user;

import com.google.common.base.Strings;
import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.controller.response.TipoUsuarioResponse;

import java.util.Arrays;
import java.util.List;

@ToString
public class UserRequest implements IRequest {
	/* Obligatorios */
	public String username;
	public String password;
	public String nombre;
	public String email;
	public TipoUsuarioResponse tipoUsuario;

	/* Opcionales */
	public RecicladorUrbanoRequest reciclador;
	public String razonSocial;
	public Long zonaId;
	public Long organizacionId;

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
		if( !Strings.isNullOrEmpty(razonSocial) && !TipoUsuarioResponse.ORGANIZACION.equals(tipoUsuario) )
			validation.add("Si se indica la razon social, el tipo de usuario debe ser ORGANIZACION");
		if( Strings.isNullOrEmpty(razonSocial) && TipoUsuarioResponse.ORGANIZACION.equals(tipoUsuario) )
			validation.add("Se debe indicar la razon social si el tipo de usuario es ORGANIZACION");
		if( organizacionId != null && (tipoUsuario == null || !List.of(TipoUsuarioResponse.RECICLADOR_URBANO, TipoUsuarioResponse.ORGANIZACION).contains(tipoUsuario)) )
			validation.add("Si se indica la organizacion, el tipo de usuario debe ser RECICLADOR_URBANO u ORGANIZACION");
		if( organizacionId == null && TipoUsuarioResponse.RECICLADOR_URBANO.equals(tipoUsuario))
			validation.add("Se debe indicar la organizacion si el tipo de usuario es RECICLADOR_URBANO u ORGANIZACION");
		if(zonaId != null && !TipoUsuarioResponse.RECICLADOR_URBANO.equals(tipoUsuario))
			validation.add("Si se indica la zona, el tipo de usuario debe ser RECICLADOR_URBANO");

		// TODO: La zona puede quedar en null de entrada, si luego se cambia validar
		return validation;
	}
}

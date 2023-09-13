package org.circle8.controller.request.user;

import com.google.common.base.Strings;
import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.controller.response.TipoUsuarioResponse;

@ToString
public class UserPutRequest implements IRequest {

	public String username;
	public String nombre;
	public String email;
	public TipoUsuarioResponse tipoUsuario;
	public String razonSocial;
	public Long zonaId;
	public Long organizacionId;
	public RecicladorUrbanoRequest reciclador;

	@Override
	public Validation valid() {
		final var validation = new Validation();
		if ( Strings.isNullOrEmpty(username)
				&& Strings.isNullOrEmpty(nombre)
				&& Strings.isNullOrEmpty(email)
				&& tipoUsuario == null
				&& Strings.isNullOrEmpty(razonSocial)
				&& zonaId == null
				&& organizacionId == null
				&& reciclador == null)
			validation.add("Debe enviar al menos un campo para actualizar");

		return validation;
	}
}

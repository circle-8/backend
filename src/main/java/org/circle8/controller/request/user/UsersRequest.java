package org.circle8.controller.request.user;

import org.circle8.controller.request.IRequest;
import org.circle8.controller.response.TipoUsuarioResponse;
import org.circle8.utils.Parser;

import java.util.List;
import java.util.Map;

public class UsersRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long organizacionId;
	public TipoUsuarioResponse tipoUsuario;

	public UsersRequest(Map<String, List<String>> queryParams) {
		this.organizacionId = Parser.parseLong(validation, queryParams, "organizacion_id");

		try {
			var tipoParams = queryParams.getOrDefault("tipo_usuario", List.of());
			this.tipoUsuario = !tipoParams.isEmpty() ? TipoUsuarioResponse.valueOf(tipoParams.get(0)) : null;
		} catch ( IllegalArgumentException e ) {
			validation.add("`tipo_usuario` debe ser un tipo de usuario válido");
		}
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

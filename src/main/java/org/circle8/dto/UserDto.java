package org.circle8.dto;

import org.circle8.controller.request.user.UserRequest;
import org.circle8.controller.response.TipoUsuarioResponse;
import org.circle8.controller.response.UserResponse;
import org.circle8.entity.User;

public class UserDto {
	public long id;
	public String username;
	public String nombre;
	public TipoUsuario tipo;
	public SuscripcionDto suscripcion;

	public static UserDto from(UserRequest request) {
		// TODO: probar ModelMapper
		var u = new UserDto();
		u.username = request.username;
		u.nombre = request.nombre;
		u.tipo = request.tipoUsuario.to();
		return u;
	}

	public static UserDto from(User entity) {
		var u = new UserDto();
		u.id = entity.id;
		u.username = entity.username;
		u.nombre = entity.nombre;
		u.tipo = entity.tipo;
		return u;
	}

	public UserResponse toResponse() {
		return new UserResponse(id, username, nombre, TipoUsuarioResponse.from(tipo), suscripcion.toResponse());
	}

	public User toEntity() {
		return User.builder()
			.id(id).username(username).nombre(nombre).tipo(tipo)
			.build();
	}
}

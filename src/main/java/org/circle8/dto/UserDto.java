package org.circle8.dto;

import org.circle8.controller.request.user.UserRequest;
import org.circle8.controller.response.TipoUsuarioResponse;
import org.circle8.controller.response.UserResponse;
import org.circle8.entity.User;

public class UserDto {
	public long id;
	public String username;
	public String nombre;
	public String email;
	public TipoUsuario tipo;
	public SuscripcionDto suscripcion;

	public static UserDto from(UserRequest request) {
		// TODO: probar ModelMapper
		var u = new UserDto();
		u.username = request.username;
		u.nombre = request.nombre;
		u.email = request.email;
		u.tipo = request.tipoUsuario.to();
		return u;
	}

	public static UserDto from(User entity) {
		var u = new UserDto();
		u.id = entity.id;
		u.username = entity.username;
		u.nombre = entity.nombre;
		u.email = entity.email;
		u.tipo = entity.tipo;
		return u;
	}

	public UserResponse toResponse() {
		return new UserResponse(
			id,
			username,
			nombre,
			email,
			TipoUsuarioResponse.from(tipo),
			suscripcion != null ? suscripcion.toResponse() : null
		);
	}

	public User toEntity() {
		return User.builder()
			.id(id).username(username).nombre(nombre).tipo(tipo).email(email)
			.build();
	}
}

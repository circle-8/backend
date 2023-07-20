package org.circle8.dto;

import org.circle8.controller.request.user.UserRequest;
import org.circle8.controller.response.TipoUsuarioResponse;
import org.circle8.controller.response.UserResponse;
import org.circle8.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
	public long id;
	public String username;
	public String nombre;
	public String email;
	public TipoUsuario tipo;
	public SuscripcionDto suscripcion;
	public Long ciudadanoId; // TODO: se puede cambiar por CiudadanoDto (nullable)

	public static UserDto from(UserRequest request) {
		val u = new UserDto();
		u.username = request.username;
		u.nombre = request.nombre;
		u.email = request.email;
		u.tipo = request.tipoUsuario.to();
		return u;
	}

	public static UserDto from(User entity) {
		if ( entity == null ) return null;
		var u = new UserDto();
		u.id = entity.id;
		u.username = entity.username;
		u.nombre = entity.nombre;
		u.email = entity.email;
		u.tipo = entity.tipo;
		u.ciudadanoId = entity.ciudadanoId;
		return u;
	}

	public UserResponse toResponse() {
		return new UserResponse(
			id,
			username,
			nombre,
			email,
			TipoUsuarioResponse.from(tipo),
			suscripcion != null ? suscripcion.toResponse() : null,
			ciudadanoId
		);
	}

	public User toEntity() {
		return User.builder()
			.id(id).username(username).nombre(nombre).tipo(tipo).email(email)
			.build();
	}
}

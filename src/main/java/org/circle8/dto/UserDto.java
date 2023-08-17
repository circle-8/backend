package org.circle8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.val;
import org.circle8.controller.request.user.UserRequest;
import org.circle8.controller.response.TipoUsuarioResponse;
import org.circle8.controller.response.UserResponse;
import org.circle8.entity.User;

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
	public Long ciudadanoId;
	public Long recicladorUrbanoId;
	public Long organizacionId;
	public Long zonaId;

	public static UserDto from(UserRequest request) {
		val u = new UserDto();
		u.username = request.username;
		u.nombre = request.nombre;
		u.email = request.email;
		u.tipo = request.tipoUsuario.to();
		u.organizacionId = request.organizacionId;
		u.zonaId = request.zonaId;
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
		u.recicladorUrbanoId = entity.recicladorUrbanoId;
		u.organizacionId = entity.organizacionId;
		u.zonaId = entity.zonaId;
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
			ciudadanoId,
			recicladorUrbanoId,
			organizacionId,
			zonaId
		);
	}

	public User toEntity() {
		return User.builder()
			.id(id)
			.username(username)
			.nombre(nombre)
			.tipo(tipo)
			.email(email)
			.organizacionId(organizacionId)
			.zonaId(zonaId)
			.build();
	}
}

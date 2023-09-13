package org.circle8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.val;
import org.circle8.controller.request.user.UserPutRequest;
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
	public RecicladorUrbanoDto reciclador;
	public Long zonaId;
	public Long organizacionId;
	public String razonSocial;
	public Long transportistaId;

	public static UserDto from(UserRequest request) {
		val u = new UserDto();
		u.username = request.username;
		u.nombre = request.nombre;
		u.email = request.email;
		u.tipo = request.tipoUsuario.to();
		u.organizacionId = request.organizacionId;
		u.reciclador = RecicladorUrbanoDto.from(request.reciclador);
		u.razonSocial = request.razonSocial;
		u.zonaId = request.zonaId;
		u.razonSocial = request.razonSocial;
		return u;
	}

	public static UserDto from(UserPutRequest request) {
		val u = new UserDto();
		u.username = request.username;
		u.nombre = request.nombre;
		u.email = request.email;
		u.tipo = request.tipoUsuario != null ? request.tipoUsuario.to() : null;
		u.organizacionId = request.organizacionId;
		u.reciclador = RecicladorUrbanoDto.from(request.reciclador);
		u.razonSocial = request.razonSocial;
		u.zonaId = request.zonaId;
		u.razonSocial = request.razonSocial;
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
		u.suscripcion = SuscripcionDto.from(entity.suscripcion);
		u.ciudadanoId = entity.ciudadanoId;
		u.recicladorUrbanoId = entity.recicladorUrbanoId;
		u.reciclador = RecicladorUrbanoDto.from(entity.reciclador);
		u.organizacionId = entity.organizacionId;
		u.transportistaId = entity.transportistaId;
		u.razonSocial = entity.razonSocial;
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
			reciclador != null ? reciclador.toResponse() : null,
			organizacionId,
			transportistaId,
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
			.transportistaId(transportistaId)
			.reciclador(reciclador != null ? reciclador.toEntity() : null)
			.razonSocial(razonSocial)
			.zonaId(zonaId)
			.build();
	}
}

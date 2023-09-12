package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserResponse implements ApiResponse {
	public long id;
	public String username;
	public String nombre;
	public String email;
	public TipoUsuarioResponse tipoUsuario;
	public SuscripcionResponse suscripcion;
	public Long ciudadanoId;
	public Long recicladorUrbanoId;
	public Long organizacionId;
	public Long transportistaId;
	public Long zonaId;
}

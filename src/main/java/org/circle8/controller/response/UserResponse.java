package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserResponse implements ApiResponse {
	public int id;
	public String username;
	public String nombre;
	public TipoUsuarioResponse tipoUsuario;
	public SuscripcionResponse suscripcion;
}

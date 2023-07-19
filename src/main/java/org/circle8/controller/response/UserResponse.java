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
	// TODO: para cuando haya que hacer el endpoint de users, se puede sumar mas info para ciudadano
	// TODO: falta agregar info extra para reciclador comunitario
}

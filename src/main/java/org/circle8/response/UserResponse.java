package org.circle8.response;

public class UserResponse implements ApiResponse {
	public int id;
	public String username;
	public TipoUsuarioResponse tipoUsuario;
	public SuscripcionResponse suscripcion;
}

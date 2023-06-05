package org.circle8.response;

import org.circle8.entity.TipoUsuario;

public enum TipoUsuarioResponse implements ApiResponse {
	CIUDADANO,
	TRANSPORTISTA,
	RECICLADOR_URBANO,
	RECICLADOR_PARTICULAR,
	ORGANIZACION;

	public static TipoUsuarioResponse from(TipoUsuario tipo) {
		return switch(tipo) {
			case CIUDADANO -> CIUDADANO;
			case TRANSPORTISTA -> TRANSPORTISTA;
			case RECICLADOR_URBANO -> RECICLADOR_URBANO;
			case RECICLADOR_PARTICULAR -> RECICLADOR_PARTICULAR;
			case ORGANIZACION -> ORGANIZACION;
		};
	}
}

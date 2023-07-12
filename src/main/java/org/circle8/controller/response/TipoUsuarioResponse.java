package org.circle8.controller.response;

import org.circle8.dto.TipoUsuario;

public enum TipoUsuarioResponse implements ApiResponse {
	CIUDADANO,
	TRANSPORTISTA,
	RECICLADOR_URBANO,
	ORGANIZACION;

	public static TipoUsuarioResponse from(TipoUsuario tipo) {
		return switch(tipo) {
			case CIUDADANO -> CIUDADANO;
			case TRANSPORTISTA -> TRANSPORTISTA;
			case RECICLADOR_URBANO -> RECICLADOR_URBANO;
			case ORGANIZACION -> ORGANIZACION;
		};
	}

	public TipoUsuario to() {
		return switch(this) {
			case CIUDADANO -> TipoUsuario.CIUDADANO;
			case TRANSPORTISTA -> TipoUsuario.TRANSPORTISTA;
			case RECICLADOR_URBANO -> TipoUsuario.RECICLADOR_URBANO;
			case ORGANIZACION -> TipoUsuario.ORGANIZACION;
		};
	}
}

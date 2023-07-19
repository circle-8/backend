package org.circle8.dto;

import org.circle8.controller.response.TransaccionResponse;
import org.circle8.entity.Transaccion;

public class TransaccionDto {
	public long id;

	public static TransaccionDto from(Transaccion transaccion) {
		if ( transaccion == null ) return null;
		var t = new TransaccionDto();
		t.id = transaccion.id;
		// TODO
		return t;
	}

	public TransaccionResponse toResponse() {
		// TODO
		return new TransaccionResponse();
	}
}

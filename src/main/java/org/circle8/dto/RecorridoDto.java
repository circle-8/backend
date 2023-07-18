package org.circle8.dto;

import org.circle8.controller.response.RecorridoResponse;
import org.circle8.entity.Recorrido;

public class RecorridoDto {
	public long id;

	public static RecorridoDto from(Recorrido recorrido) {
		if ( recorrido == null ) return null;
		var r = new RecorridoDto();
		r.id = recorrido.id;
		// TODO
		return r;
	}

	public RecorridoResponse toResponse() {
		// TODO
		return new RecorridoResponse();
	}
}

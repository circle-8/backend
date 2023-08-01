package org.circle8.dto;

import org.circle8.entity.Transportista;

public class TransportistaDto {
	public long id;
	public long usuarioId;

	public static TransportistaDto from(Transportista entity) {
		if ( entity == null ) return null;
		var t = new TransportistaDto();
		t.id = entity.id;
		t.usuarioId = entity.usuarioId;
		return t;
	}
}

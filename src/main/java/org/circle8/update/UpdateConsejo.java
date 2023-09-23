package org.circle8.update;

import lombok.AllArgsConstructor;
import org.circle8.controller.request.consejo.PutConsejoRequest;

@AllArgsConstructor
public class UpdateConsejo {
	public long id;
	public String titulo;
	public String descripcion;

	public static UpdateConsejo from(PutConsejoRequest req) {
		return new UpdateConsejo(req.id, req.titulo, req.descripcion);
	}
}

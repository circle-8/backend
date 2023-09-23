package org.circle8.controller.request.consejo;

import lombok.val;
import org.circle8.controller.request.IRequest;

public class PutConsejoRequest implements IRequest {
	public Long id;
	public String titulo;
	public String descripcion;

	@Override
	public Validation valid() {
		val v = new Validation();
		if ( id == null ) v.add("se debe especificar `id`");
		return v;
	}
}

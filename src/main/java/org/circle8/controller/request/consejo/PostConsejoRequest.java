package org.circle8.controller.request.consejo;

import com.google.common.base.Strings;
import lombok.val;
import org.circle8.controller.request.IRequest;

public class PostConsejoRequest implements IRequest {
	public String titulo;
	public String descripcion;

	@Override
	public Validation valid() {
		val v = new Validation();

		if ( Strings.isNullOrEmpty(titulo) )
			v.add("`titulo` no debe estar vacio");
		if ( Strings.isNullOrEmpty(descripcion) )
			v.add("`descripcion` no debe estar vacio");

		return v;
	}
}

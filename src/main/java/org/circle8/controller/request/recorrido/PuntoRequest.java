package org.circle8.controller.request.recorrido;

import org.circle8.controller.request.IRequest;

import lombok.val;

public class PuntoRequest implements IRequest {
	public Double latitud;
	public Double longitud;

	@Override
	public Validation valid() {
		val validation = new Validation();

		if ( latitud == null )
			validation.add("Se debe especificar la latitud");
		if ( longitud == null )
			validation.add("Se debe especificar la longitud");
		return validation;
	}
}

package org.circle8.controller.request.recorrido;

import java.time.LocalDate;

import org.circle8.controller.request.IRequest;

import lombok.val;

public class PutRecorridoRequest implements IRequest {

	public LocalDate fechaRetiro;
	public Long recicladorId;

	@Override
	public Validation valid() {
		val validation = new Validation();

		if ( recicladorId == null && fechaRetiro == null)
			validation.add("No puede estar vacío tanto 'recicladorId' como 'fechaRetiro'");

		return validation;
	}
}

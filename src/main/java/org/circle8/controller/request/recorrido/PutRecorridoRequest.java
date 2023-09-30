package org.circle8.controller.request.recorrido;

import lombok.val;
import org.circle8.controller.request.IRequest;
import org.circle8.entity.Punto;

import java.time.LocalDate;

public class PutRecorridoRequest implements IRequest {

	public LocalDate fechaRetiro;
	public Long recicladorId;
	public Punto puntoInicio;
	public Punto puntoFin;

	@Override
	public Validation valid() {
		val validation = new Validation();

		if ( recicladorId == null && fechaRetiro == null)
			validation.add("No puede estar vacío tanto 'recicladorId' como 'fechaRetiro'");

		return validation;
	}
}

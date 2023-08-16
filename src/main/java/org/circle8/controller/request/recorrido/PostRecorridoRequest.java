package org.circle8.controller.request.recorrido;

import lombok.val;
import org.circle8.controller.request.IRequest;
import org.circle8.entity.Punto;

import java.time.LocalDate;

public class PostRecorridoRequest implements IRequest {
	public Punto puntoInicio;
	public Punto puntoFin;
	public Long recicladorId;
	public LocalDate fechaRetiro;

	@Override
	public Validation valid() {
		val validation = new Validation();

		if ( puntoInicio == null )
			validation.add("'puntoInicio' no debe estar vacío");
		if ( puntoFin == null )
			validation.add("'puntoFin' no debe estar vacío");
		if ( recicladorId == null )
			validation.add("'recicladorId' no debe estar vacío");
		if ( fechaRetiro == null )
			validation.add("'fechaRetiro' no debe estar vacío");

		return validation;
	}
}

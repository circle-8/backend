package org.circle8.controller.request.plan;

import java.math.BigDecimal;

import org.circle8.controller.request.IRequest;

import lombok.val;

public class PutPlanRequest implements IRequest {

	public String nombre;
	public BigDecimal precio;
	public Integer mesesRenovacion;
	public Integer cantUsuarios;

	@Override
	public Validation valid() {
		val validation = new Validation();

		if(nombre == null && precio == null && mesesRenovacion == null && cantUsuarios == null) {
			validation.add("Se debe especificar al menos un campo para actualizar");
		}

		return validation;
	}
}

package org.circle8.controller.request.plan;

import java.math.BigDecimal;

import org.circle8.controller.request.IRequest;

import lombok.val;

public class PostPlanRequest implements IRequest {

	public String nombre;
	public BigDecimal precio;
	public Integer mesesRenovacion;
	public Integer cantUsuarios;

	@Override
	public Validation valid() {
		val validation = new Validation();

		if(nombre == null){
			validation.add("'nombre' no puede estar vacio");
		}
		if(precio == null) {
			validation.add("'precio' no puede estar vacio");
		}
		if(mesesRenovacion == null) {
			validation.add("'mesesRenovacion' no puede estar vacio");
		}
		if(cantUsuarios == null) {
			validation.add("'cantUsuarios' no puede estar vacio");
		}

		return validation;
	}
}

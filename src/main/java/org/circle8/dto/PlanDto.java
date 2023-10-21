package org.circle8.dto;

import java.math.BigDecimal;

import org.circle8.controller.response.PlanResponse;
import org.circle8.entity.Plan;

public class PlanDto {
	public long id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int cantidadUsuarios;

	public static PlanDto from(Plan entity) {
		if ( entity == null ) return null;
		var p = new PlanDto();
		p.id = entity.id;
		p.nombre = entity.nombre;
		p.precio = entity.precio;
		p.mesesRenovacion = entity.mesesRenovacion;
		p.cantidadUsuarios = entity.cantidadUsuarios;
		return p;
	}

	public PlanResponse toResponse() {
		return PlanResponse.builder()
			.id(id)
			.nombre(nombre)
			.precio(precio)
			.mesesRenovacion(mesesRenovacion)
			.cantidadUsuarios(cantidadUsuarios)
			.build();
	}

    public Plan toEntity() {
		return Plan.builder()
			.id(id)
			.nombre(nombre)
			.precio(precio)
			.mesesRenovacion(mesesRenovacion)
			.cantidadUsuarios(cantidadUsuarios)
			.build();
    }
}

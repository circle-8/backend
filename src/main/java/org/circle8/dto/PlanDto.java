package org.circle8.dto;

import java.math.BigDecimal;

import org.circle8.controller.response.PlanResponse;
import org.circle8.entity.Plan;

public class PlanDto {
	public long id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int cantUsuarios;

	public static PlanDto from(Plan entity) {
		if ( entity == null ) return null;
		var p = new PlanDto();
		p.id = entity.id;
		p.cantUsuarios = entity.cantUsuarios;
		p.nombre = entity.nombre;
		p.mesesRenovacion = entity.mesesRenovacion;
		p.precio = entity.precio;
		return p;
	}

	public PlanResponse toResponse() {
		return PlanResponse.builder()
			.id(id)
			.mesesRenovacion(mesesRenovacion)
			.nombre(nombre)
			.precio(precio)
			.cantUsuarios(cantUsuarios)
			.build();
	}

    public Plan toEntity() {
		return Plan.builder()
			.id(id)
		   .mesesRenovacion(mesesRenovacion)
		   .nombre(nombre)
		   .precio(precio)
		   .cantUsuarios(cantUsuarios)
		   .build();
    }
}

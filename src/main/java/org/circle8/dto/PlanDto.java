package org.circle8.dto;

import java.math.BigDecimal;

import org.circle8.controller.request.plan.PostPlanRequest;
import org.circle8.controller.request.plan.PutPlanRequest;
import org.circle8.controller.response.PlanResponse;
import org.circle8.entity.Plan;

public class PlanDto {
	public long id;
	public String nombre;
	public BigDecimal precio;
	public Integer mesesRenovacion;
	public Integer cantUsuarios;

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

	public static PlanDto from(PostPlanRequest req) {
		if ( req == null ) return null;
		var p = new PlanDto();
		p.cantUsuarios = req.cantUsuarios;
		p.nombre = req.nombre;
		p.mesesRenovacion = req.mesesRenovacion;
		p.precio = req.precio;
		return p;
	}

	public static PlanDto from(PutPlanRequest req) {
		if ( req == null ) return null;
		var p = new PlanDto();
		p.cantUsuarios = req.cantUsuarios != null ? req.cantUsuarios : null;
		p.nombre = req.nombre;
		p.mesesRenovacion = req.mesesRenovacion!= null ? req.mesesRenovacion : null;
		p.precio = req.precio!= null ? req.precio : null;
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

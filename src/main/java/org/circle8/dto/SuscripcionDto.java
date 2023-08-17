package org.circle8.dto;

import org.circle8.controller.response.SuscripcionResponse;
import org.circle8.entity.Suscripcion;

import java.time.LocalDateTime;

public class SuscripcionDto {
	public long id;
	public LocalDateTime ultimaRenovacion;
	public LocalDateTime proximaRenovacion;
	public PlanDto plan;

	public static SuscripcionDto from(Suscripcion entity) {
		if ( entity == null ) return null;
		var s = new SuscripcionDto();
		s.id = entity.id;
		return s;
	}

	public SuscripcionResponse toResponse() {
		return SuscripcionResponse.builder()
			.id(id)
			.proximaRenovacion(proximaRenovacion)
			.ultimaRenovacion(ultimaRenovacion)
			.plan(plan != null ? plan.toResponse() : null)
			.build();
	}

	public Suscripcion toEntity() {
		return Suscripcion.builder()
			.id(id)
			.build();
	}
}

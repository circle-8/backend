package org.circle8.dto;

import java.time.LocalDateTime;

import org.circle8.controller.response.SuscripcionResponse;
import org.circle8.entity.Suscripcion;

public class SuscripcionDto {
	public long id;
	public LocalDateTime ultimaRenovacion;
	public LocalDateTime proximaRenovacion;
	public PlanDto plan;

	public static SuscripcionDto from(Suscripcion entity) {
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

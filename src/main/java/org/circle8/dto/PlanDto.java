package org.circle8.dto;

import org.circle8.controller.response.PlanResponse;
import org.circle8.entity.Plan;

public class PlanDto {
	public long id;

	public static PlanDto from(Plan entity) {
		var p = new PlanDto();
		p.id = entity.id;
		return p;
	}

	public PlanResponse toResponse() {
		return PlanResponse.builder()
			.id(id)
			.build();
	}
}

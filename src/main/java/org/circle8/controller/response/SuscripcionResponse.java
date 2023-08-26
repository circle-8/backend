package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SuscripcionResponse implements ApiResponse {
	public long id;
	public LocalDate ultimaRenovacion;
	public LocalDate proximaRenovacion;
	public PlanResponse plan;
}

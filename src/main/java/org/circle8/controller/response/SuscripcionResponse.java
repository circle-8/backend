package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SuscripcionResponse implements ApiResponse {
	public long id;
	public LocalDateTime ultimaRenovacion;
	public LocalDateTime proximaRenovacion;
	public PlanResponse plan;
}

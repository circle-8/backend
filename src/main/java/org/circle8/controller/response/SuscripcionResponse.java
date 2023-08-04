package org.circle8.controller.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SuscripcionResponse implements ApiResponse {
	public long id;
	public LocalDateTime ultimaRenovacion;
	public LocalDateTime proximaRenovacion;
	public PlanResponse plan;
}

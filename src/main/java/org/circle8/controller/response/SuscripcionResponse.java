package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SuscripcionResponse implements ApiResponse {
	public int id;
	public LocalDateTime ultimaRenovacion;
	public LocalDateTime proximaRenovacion;
	public PlanResponse plan;
}

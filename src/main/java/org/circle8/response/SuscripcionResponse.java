package org.circle8.response;

import java.time.LocalDateTime;

public class SuscripcionResponse implements ApiResponse {
	public int id;
	public LocalDateTime ultimaRenovacion;
	public LocalDateTime proximaRenovacion;
	public PlanResponse plan;
}

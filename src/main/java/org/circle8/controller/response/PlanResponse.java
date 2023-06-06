package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlanResponse implements ApiResponse {
	public long id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int usuariosSz;
}

package org.circle8.controller.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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

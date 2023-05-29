package org.circle8.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlanResponse implements ApiResponse {
	public int id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int usuariosSz;
}

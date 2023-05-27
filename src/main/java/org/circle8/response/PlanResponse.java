package org.circle8.response;

import java.math.BigDecimal;

public class PlanResponse implements ApiResponse {
	public int id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int usuariosSz;
}

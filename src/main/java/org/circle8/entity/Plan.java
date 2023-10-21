package org.circle8.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Plan {
	public static final Plan FREE_TRIAL = new Plan(1L,"Free",BigDecimal.ZERO,0,0);
	
	public long id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int cantidadUsuarios;
}

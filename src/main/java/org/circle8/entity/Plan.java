package org.circle8.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Plan {
	public long id;
	public String nombre;
	public BigDecimal precio;
	public int mesesRenovacion;
	public int cantUsuarios;
}

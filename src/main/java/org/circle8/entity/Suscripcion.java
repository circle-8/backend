package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
public class Suscripcion {
	public long id;
	public LocalDate ultimaRenovacion;
	public LocalDate proximaRenovacion;
	public Plan plan;

	public Suscripcion(long id) { this.id = id; }
}

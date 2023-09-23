package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consejo {
	public Long id;
	public String titulo;
	public String descripcion;
	public LocalDate fechaCreacion;
}

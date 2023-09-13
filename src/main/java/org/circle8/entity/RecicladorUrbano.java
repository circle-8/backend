package org.circle8.entity;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class RecicladorUrbano {
	public long id;
	public long usuarioId;
	public long organizacionId;
	public Long zonaId;
	public LocalDate fechaNacimiento;
	public String dni;
	public String domicilio;
	public String telefono;
}

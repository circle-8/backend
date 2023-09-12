package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecicladorUrbanoResponse implements ApiResponse {
	public long id;
	public long usuarioId;
	public long organizacionId;
	public Long zonaId;
	public LocalDate fechaNacimiento;
	public String dni;
	public String domicilio;
	public String telefono;
}

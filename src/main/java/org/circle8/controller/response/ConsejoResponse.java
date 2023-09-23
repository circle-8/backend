package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ConsejoResponse implements ApiResponse {
	public long id;
	public String titulo;
	public String descripcion;
	public LocalDate fechaCreacion;
}

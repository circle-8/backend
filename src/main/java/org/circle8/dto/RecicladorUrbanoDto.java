package org.circle8.dto;

import org.circle8.controller.request.user.RecicladorUrbanoRequest;
import org.circle8.controller.response.RecicladorUrbanoResponse;
import org.circle8.entity.RecicladorUrbano;

import java.time.LocalDate;

public class RecicladorUrbanoDto {
	public long id;
	public long usuarioId;
	public long organizacionId;
	public Long zonaId;
	public LocalDate fechaNacimiento;
	public String dni;
	public String domicilio;
	public String telefono;

	public static RecicladorUrbanoDto from(RecicladorUrbanoRequest req) {
		if ( req == null ) return null;
		var r = new RecicladorUrbanoDto();
		r.fechaNacimiento = req.fechaNacimiento;
		r.dni = req.dni;
		r.domicilio = req.domicilio;
		r.telefono = req.telefono;
		return r;
	}

	public static RecicladorUrbanoDto from(RecicladorUrbano entity) {
		if ( entity == null ) return null;
		var r = new RecicladorUrbanoDto();
		r.id = entity.id;
		r.usuarioId = entity.usuarioId;
		r.organizacionId = entity.organizacionId;
		r.zonaId = entity.zonaId;
		r.fechaNacimiento = entity.fechaNacimiento;
		r.dni = entity.dni;
		r.domicilio = entity.domicilio;
		r.telefono = entity.telefono;
		return r;
	}

	public RecicladorUrbanoResponse toResponse() {
		return new RecicladorUrbanoResponse(
			this.id,
			this.usuarioId,
			this.organizacionId,
			this.zonaId,
			this.fechaNacimiento,
			this.dni,
			this.domicilio,
			this.telefono
		);
	}

	public RecicladorUrbano toEntity() {
		return new RecicladorUrbano(
			this.id,
			this.usuarioId,
			this.organizacionId,
			this.zonaId,
			this.fechaNacimiento,
			this.dni,
			this.domicilio,
			this.telefono
		);
	}
}

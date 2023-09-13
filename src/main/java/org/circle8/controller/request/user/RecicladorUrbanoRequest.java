package org.circle8.controller.request.user;

import lombok.ToString;
import org.circle8.controller.request.IRequest;

import java.time.LocalDate;

@ToString
public class RecicladorUrbanoRequest implements IRequest {
	public LocalDate fechaNacimiento;
	public String dni;
	public String domicilio;
	public String telefono;

	@Override
	public Validation valid() {
		return new Validation(); // all field are optional
	}
}

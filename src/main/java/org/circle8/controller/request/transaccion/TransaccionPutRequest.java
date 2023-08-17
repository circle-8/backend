package org.circle8.controller.request.transaccion;

import org.circle8.controller.request.IRequest;

import java.time.ZonedDateTime;

public class TransaccionPutRequest implements IRequest{
	private final IRequest.Validation validation = new IRequest.Validation();

	public Long id;
	public ZonedDateTime fechaRetiro;

	@Override
	public Validation valid() {
		if(fechaRetiro == null)
			validation.add("la fecha de retiro debe ser especificada");
		return validation;
	}

}

package org.circle8.controller.request.transaccion;

import org.circle8.controller.request.IRequest;

import java.util.List;

public class TransaccionPostRequest implements IRequest{
	private final IRequest.Validation validation = new IRequest.Validation();

	// TODO solicitud Id
	public Long puntoReciclaje;
	public List<Long> residuoId;

	@Override
	public IRequest.Validation valid() {
		if(puntoReciclaje == null)
			validation.add("El punto de reciclaje no puede ser nulo");
		if(residuoId == null || residuoId.isEmpty())
			validation.add("Se debe especificar al menos el id de un residuo");
		return validation;
	}
}

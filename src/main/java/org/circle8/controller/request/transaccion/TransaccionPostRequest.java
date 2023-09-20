package org.circle8.controller.request.transaccion;

import org.circle8.controller.request.IRequest;

import java.util.List;

public class TransaccionPostRequest implements IRequest{
	private final IRequest.Validation validation = new IRequest.Validation();

	public Long puntoReciclaje; // TODO: esto queda mal, deberia ser puntoReciclajeId
	public List<Long> residuoId;
	public Long solicitudId;

	@Override
	public IRequest.Validation valid() {
		if(puntoReciclaje == null)
			validation.add("Se debe especificar el `puntoReciclaje`");
		if(residuoId == null || residuoId.isEmpty())
			validation.add("Se debe especificar al menos un `residuoId`");
		if ( solicitudId == null )
			validation.add("Se debe especificar `solicitudId`");
		return validation;
	}
}

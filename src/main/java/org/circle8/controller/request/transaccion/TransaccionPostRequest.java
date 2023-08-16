package org.circle8.controller.request.transaccion;

import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

public class TransaccionPostRequest implements IRequest{
	private final IRequest.Validation validation = new IRequest.Validation();

	public Long puntoReciclajeId;
	public List<Long> residuosId;

	public TransaccionPostRequest(Map<String, List<String>> queryParams) {
		this.puntoReciclajeId = Parser.parseLong(validation, queryParams, "punto_reciclaje");
		this.residuosId = queryParams.getOrDefault("residuo", List.of()).stream().map(Long::parseLong).toList();
	}

	@Override
	public IRequest.Validation valid() {
		if(puntoReciclajeId == null)
			validation.add("El punto de reciclaje no puede ser nulo");
		if(residuosId == null || residuosId.isEmpty())
			validation.add("Se debe especificar al menos el id de un residuo");
		return validation;
	}
}

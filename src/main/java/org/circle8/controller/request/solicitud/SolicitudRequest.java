package org.circle8.controller.request.solicitud;

import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

public class SolicitudRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long solicitanteId;
	public Long solicitadoId;

	public SolicitudRequest(Map<String, List<String>> queryParams) {		
		this.solicitanteId = Parser.parseLong(validation,queryParams, "solicitanteId");
		this.solicitadoId = Parser.parseLong(validation,queryParams, "solicitadoId");
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

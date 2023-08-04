package org.circle8.controller.request.solicitud;

import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

import lombok.ToString;

@ToString
public class SolicitudRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long solicitanteId;
	public Long solicitadoId;

	public List<String> expands;

	public SolicitudRequest(Map<String, List<String>> queryParams) {
		this.solicitanteId = Parser.parseLong(validation, queryParams, "solicitante_id");
		this.solicitadoId = Parser.parseLong(validation, queryParams, "solicitado_id");
		this.expands = queryParams.getOrDefault("expand", List.of());
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

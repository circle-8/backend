package org.circle8.controller.request.transaccion;

import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

public class TransaccionesRequest {

	private final IRequest.Validation validation = new IRequest.Validation();

	public Long transportistaId;
	public List<Long> puntosReciclaje;
	public List<String> expands;

	public TransaccionesRequest(Map<String, List<String>> queryParams) {
		this.transportistaId = Parser.parseLong(validation, queryParams, "transportista");
		this.puntosReciclaje = queryParams.getOrDefault("punto_reciclaje", List.of()).stream().map(Long::parseLong).toList();
		this.expands = queryParams.getOrDefault("expand", List.of());
	}

	public IRequest.Validation valid() {
		return validation;
	}
}

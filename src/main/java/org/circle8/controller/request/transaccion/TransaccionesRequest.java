package org.circle8.controller.request.transaccion;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

import java.util.List;
import java.util.Map;

public class TransaccionesRequest {

	private final IRequest.Validation validation = new IRequest.Validation();

	public Long transportistaId;
	public Long ciudadanoId;
	public Boolean conTransporte;
	public List<Long> puntosReciclaje;
	public List<String> expands;

	public TransaccionesRequest(Map<String, List<String>> queryParams) {
		this.transportistaId = Parser.parseLong(validation, queryParams, "transportista_id");
		this.ciudadanoId = Parser.parseLong(validation, queryParams, "ciudadano_id");
		this.conTransporte = Parser.parseBoolean(validation, queryParams, "con_transporte");
		this.puntosReciclaje = queryParams.getOrDefault("punto_reciclaje", List.of()).stream().map(Long::parseLong).toList();
		this.expands = queryParams.getOrDefault("expand", List.of());
	}

	public IRequest.Validation valid() {
		return validation;
	}
}

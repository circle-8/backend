package org.circle8.controller.request.zona;

import java.util.List;
import java.util.Map;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

@ToString
public class ZonaRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long organizacionId;
	public final boolean organizacion;
	public final boolean recorridos;

	public ZonaRequest(Map<String, List<String>> queryParams) {
		this.organizacionId = Parser.parseLong(validation, queryParams, "organizacion_id");
		List<String> expands = queryParams.getOrDefault("expand", List.of());
		this.organizacion = expands.contains("organizacion");
		this.recorridos = expands.contains("recorridos");
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

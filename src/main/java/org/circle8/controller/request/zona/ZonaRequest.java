package org.circle8.controller.request.zona;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

import java.util.List;
import java.util.Map;

@ToString
public class ZonaRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long organizacionId;
	public Long recicladorId;
	public Long ciudadanoId;
	public Long puntoResiduoId;
	public List<Integer> tiposResiduo;

	public ZonaRequest(Map<String, List<String>> queryParams) {
		this.organizacionId = Parser.parseLong(validation, queryParams, "organizacion_id");
		this.recicladorId = Parser.parseLong(validation, queryParams, "reciclador_id");
		this.ciudadanoId = Parser.parseLong(validation, queryParams, "ciudadano_id");
		this.puntoResiduoId = Parser.parseLong(validation, queryParams, "punto_residuo_id");
		try {
			this.tiposResiduo = queryParams.getOrDefault("tipos_residuo", List.of())
				.stream()
				.map(Integer::parseInt)
				.toList();
		} catch (NumberFormatException e) {
			validation.add("'tipos_residuo' deben ser números representando al id del tipo.");
		}
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

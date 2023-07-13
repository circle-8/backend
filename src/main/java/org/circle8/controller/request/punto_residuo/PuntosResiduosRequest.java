package org.circle8.controller.request.punto_residuo;

import org.circle8.controller.request.IRequest;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class PuntosResiduosRequest implements IRequest {
	private final Validation validation = new Validation();

	public Double latitud;
	public Double longitud;
	public Double radio;

	public List<String> tipoResiduo;

	public List<String> expands;

	public PuntosResiduosRequest(Map<String, List<String>> queryParams) {
		this.latitud = parse(queryParams, "latitud");
		this.longitud = parse(queryParams, "longitud");
		this.radio = parse(queryParams, "radio");

		this.tipoResiduo = queryParams.getOrDefault("tipos_residuo", List.of());
		this.expands = queryParams.getOrDefault("expand", List.of());
	}

	@Nullable
	private Double parse(Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Double.parseDouble(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Override
	public Validation valid() {
		if ( latitud != null && (longitud == null || radio == null) )
			validation.add("Si se especifica latitud, se debe enviar longitud y radio");
		if ( longitud != null && (latitud == null || radio == null) )
			validation.add("Si se especifica longitud, se debe enviar latitud y radio");
		if ( radio != null && (latitud == null || longitud == null) )
			validation.add("Si se especifica radio, se debe enviar latitud y longitud");

		return validation;
	}
}

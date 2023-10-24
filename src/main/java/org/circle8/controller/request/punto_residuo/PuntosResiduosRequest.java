package org.circle8.controller.request.punto_residuo;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

import java.util.List;
import java.util.Map;

@ToString
public class PuntosResiduosRequest implements IRequest {
	private final Validation validation = new Validation();

	public Double latitud;
	public Double longitud;
	public Double radio;
	public Long ciudadanoId;
	public Long notCiudadanoId;

	public List<String> tipoResiduo;

	public List<String> expands;

	public PuntosResiduosRequest(Map<String, List<String>> queryParams) {
		this.latitud = Parser.parseDouble(validation, queryParams, "latitud");
		this.longitud = Parser.parseDouble(validation, queryParams, "longitud");
		this.radio = Parser.parseDouble(validation, queryParams, "radio");
		this.ciudadanoId = Parser.parseLong(validation, queryParams, "ciudadano_id");
		this.notCiudadanoId = Parser.parseLong(validation, queryParams, "not_ciudadano_id");

		this.tipoResiduo = queryParams.getOrDefault("tipos_residuo", List.of());
		this.expands = queryParams.getOrDefault("expand", List.of());
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

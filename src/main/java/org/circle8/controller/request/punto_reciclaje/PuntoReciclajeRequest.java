package org.circle8.controller.request.punto_reciclaje;

import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import org.circle8.controller.request.IRequest;

import jakarta.annotation.Nullable;

public class PuntoReciclajeRequest implements IRequest {
	private final Validation validation = new Validation();

	public List<Integer> dias;
	public List<String> tiposResiduo;
	public Long recicladorId;
	public Double latitud;
	public Double longitud;
	public Double radio;

	public PuntoReciclajeRequest(Map<String, List<String>> queryParams) {
		try {
			this.dias = queryParams.getOrDefault("dias", List.of())
				.stream()
				.map(Integer::parseInt)
				.toList();
		} catch ( NumberFormatException e ) {
			validation.add("'dias' deben ser números del 0 al 6. Comenzando por LUNES.");
		}

		this.tiposResiduo = queryParams.getOrDefault("tipos_residuo", List.of());
		//TODO cuando mergeemos el PR de Ger deberiamos usar Parser
		this.recicladorId = parseLong(queryParams, "reciclador_id");
		this.latitud = parseDouble(queryParams, "latitud");
		this.longitud = parseDouble(queryParams, "longitud");
		this.radio = parseDouble(queryParams, "radio");
	}

	@Nullable
	private Long parseLong(Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Long.parseLong(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	private Integer parseInt(Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Integer.parseInt(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	private Double parseDouble(Map<String, List<String>> queryParams, String paramName) {
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

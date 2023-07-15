package org.circle8.controller.request.punto_reciclaje;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.units.qual.N;
import org.circle8.controller.request.IRequest;

import jakarta.annotation.Nullable;

public class PuntoReciclajeRequest implements IRequest {
	private final Validation validation = new Validation();

	public List<Integer> dias;
	public List<String> tiposResiduo; //TODO: definir cómo viajan los tipo de residuo front->back
	public Long recicladorId;  //TODO: cómo se obtiene el valor de {reciclador_id} para el POST
	public Double latitud;
	public Double longitud;
	public Double radio;
	public String titulo;

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
		this.recicladorId = parseInt(queryParams, "reciclador_id");
		this.latitud = parseDouble(queryParams, "latitud");
		this.longitud = parseDouble(queryParams, "longitud");
		this.radio = parseDouble(queryParams, "radio");
		this.titulo = parseString(queryParams, "titulo");
	}

	@Nullable
	private Long parseInt(Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Long.parseLong(param.get(0)) : null;
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

	@Nullable
	private String parseString(Map<String, List<String>> queryParams, String paramName) {
		var param = queryParams.getOrDefault(paramName, List.of());
		return !param.isEmpty() ? param.get(0).toString() : null;
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

	public Validation validForPost() {
		if( latitud == null || longitud == null) {
			validation.add("Se debe especificar tanto latitud como longitud");
		}
		if( recicladorId == null ) {
			validation.add("Se debe especificar el id del reciclador");
		}
		if( dias.isEmpty()) {
			validation.add("Se deben especificar los días disponibles");
		}
		if( titulo == null || titulo.isEmpty()) {
			validation.add("Se debe especificar el titulo del punto");
		}
		return validation;
	}

}

package org.circle8.controller.request.punto_reciclaje;

import jakarta.annotation.Nullable;
import org.circle8.controller.request.IRequest;

import java.util.List;
import java.util.Map;

public class PuntoReciclajePostRequest implements IRequest {
	private final Validation validation = new Validation();

	public List<Integer> dias;
	public List<Integer> tiposResiduo;
	public Long recicladorId;
	public Double latitud;
	public Double longitud;
	public String titulo;

	public PuntoReciclajePostRequest(Map<String, List<String>> queryParams) {
		try {
			this.dias = queryParams.getOrDefault("dias", List.of())
				.stream()
				.map(Integer::parseInt)
				.toList();
		} catch (NumberFormatException e) {
			validation.add("'dias' deben ser números del 0 al 6. Comenzando por LUNES.");
		}
		try {
			this.tiposResiduo = queryParams.getOrDefault("tipos_residuo", List.of())
				.stream()
				.map(Integer::parseInt)
				.toList();
		} catch (NumberFormatException e) {
			validation.add("'tipos_residuo' deben ser números representando al id del tipo.");
		}
		this.recicladorId = parseLong(queryParams, "reciclador_id");
		this.latitud = parseDouble(queryParams, "latitud");
		this.longitud = parseDouble(queryParams, "longitud");
		this.titulo = parseString(queryParams, "titulo");
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

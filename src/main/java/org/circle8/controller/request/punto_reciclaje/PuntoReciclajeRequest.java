package org.circle8.controller.request.punto_reciclaje;

import java.util.List;
import java.util.Map;

import org.circle8.controller.request.user.IRequest;

import jakarta.annotation.Nullable;

public class PuntoReciclajeRequest implements IRequest{
	private final Validation validation = new Validation();
	
	public String dias;
	public String tipoResiduo;
	public Long reciclador_id;
	public Double latitud;
	public Double longitud;
	public Double radio;	
	
	public PuntoReciclajeRequest(Map<String, List<String>> queryParams) {
		this.dias = parseString(queryParams, "dias");
		this.tipoResiduo = parseString(queryParams, "tipo_residuo");
		this.reciclador_id = parseInt(queryParams, "reciclador_id");
		this.latitud = parseDouble(queryParams, "latitud");
		this.longitud = parseDouble(queryParams, "longitud");
		this.radio = parseDouble(queryParams, "radio");	
	}
	
	@Nullable
	private String parseString(Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? param.get(0) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
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

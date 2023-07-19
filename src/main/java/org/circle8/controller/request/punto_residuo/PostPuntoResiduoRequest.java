package org.circle8.controller.request.punto_residuo;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.circle8.controller.request.IRequest;

import io.javalin.http.Context;

public class PostPuntoResiduoRequest implements IRequest {
	private final Validation validation = new Validation();
	
	public Long ciudadanoId;
	public Double latitud;
	public Double longitud;
	
	public PostPuntoResiduoRequest(Context ctx) {
		var queryParams = ctx.queryParamMap();
		try {
			ciudadanoId = Long.parseLong(ctx.pathParam("ciudadano_id"));
		} catch ( NumberFormatException e) {
			validation.add("El id del ciudadano debe ser numérico");
		}		
		this.latitud = parseDouble(queryParams, "latitud");
		this.longitud = parseDouble(queryParams, "longitud");
	}
	
	//TODO: usar el parser de utils cuando se mergee
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
		if ( latitud == null )
			validation.add("Se debe especificar la latitud");
		if ( longitud == null )
			validation.add("Se debe especificar la longitud");
		return validation;
	}

}

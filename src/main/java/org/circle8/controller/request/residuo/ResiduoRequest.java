package org.circle8.controller.request.residuo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest;
import org.circle8.utiles.Parser;

public class ResiduoRequest implements IRequest{
	private final Validation validation = new Validation();
	
	public List<Integer> puntosResiduo;
	public List<Integer> ciudadanos;
	public List<String> tiposResiduo;
	public Long transaccionId;
	public Long recorridoId;
	public Long tipoResiduoId;
	public Long puntoResiduo;
	public LocalDateTime fechaLimite;
	public String descripcion;
	

	public ResiduoRequest(Map<String, List<String>> queryParams) {
		try {
			this.puntosResiduo = queryParams.getOrDefault("puntos_residuo", List.of())
				.stream()
				.map(Integer::parseInt)
				.toList();
		} catch ( NumberFormatException e ) {
			validation.add("'puntos_residuo' deben ser numeros.");
		}
		
		try {
			this.ciudadanos = queryParams.getOrDefault("ciudadanos", List.of())
				.stream()
				.map(Integer::parseInt)
				.toList();
		} catch ( NumberFormatException e ) {
			validation.add("'ciudadanos' deben ser numeros.");
		}
		
		this.tiposResiduo = queryParams.getOrDefault("tipo", List.of());
		this.transaccionId = Parser.parseLong(validation, queryParams, "transaccion");
		this.recorridoId = Parser.parseLong(validation, queryParams, "recorrido");
		this.tipoResiduoId = Parser.parseLong(validation, queryParams, "tipo_residuo");
		this.puntoResiduo = Parser.parseLong(validation, queryParams, "punto_residuo");
		this.fechaLimite = Parser.parseLocalDateTime(validation, queryParams, "fecha_limite_retiro");
		this.descripcion = Parser.parseString(validation, queryParams, "descripcion");
	}

	@Override
	public Validation valid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Validation validForPost() {
		if(tipoResiduoId == null)
			validation.add("Se debe especificar el tipo de residuo");
		if(puntoResiduo == null)
			validation.add("Se debe especificar el punto de reciduo");
		return validation;
	}

}

package org.circle8.controller.request.residuo;

import java.util.List;
import java.util.Map;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

@ToString
public class ResiduoRequest implements IRequest {
	private final Validation validation = new Validation();

	public List<Integer> puntosResiduo;
	public List<Integer> ciudadanos;
	public List<String> tiposResiduo;
	public Long transaccionId;
	public Long recorridoId;

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
		this.transaccionId = Parser.parseLong(validation, queryParams, "transaccion_id");
		this.recorridoId = Parser.parseLong(validation, queryParams, "recorrido_id");
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

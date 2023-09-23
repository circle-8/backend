package org.circle8.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.circle8.utils.Parser;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResiduosFilter {
	public List<Long> puntosResiduo;
	public List<Long> ciudadanos;
	public List<Long> tipos;
	public Long transaccion;
	public Long recorrido;
	public Boolean retirado;
	public ZonedDateTime fechaLimiteRetiro;

	public ResiduosFilter(Map<String, List<String>> queryParams) {
		this.puntosResiduo = Parser.parseLongList(queryParams.getOrDefault("puntos_residuo", List.of()));
		this.ciudadanos = Parser.parseLongList(queryParams.getOrDefault("ciudadanos", List.of()));
		this.tipos = Parser.parseLongList(queryParams.getOrDefault("tipos", List.of()));
		this.transaccion = Parser.parseLong(queryParams, "transaccion");
		this.recorrido = Parser.parseLong(queryParams, "recorrido");
		this.retirado = queryParams.containsKey("retirado") ? Boolean.valueOf(queryParams.get("retirado").get(0)) : null;
		this.fechaLimiteRetiro = Parser.parseLocalZonedDateTime(queryParams, "fecha_limite_retiro");
	}
}

package org.circle8.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.circle8.utils.Parser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RecorridoFilter {
	public static final RecorridoFilter EMPTY = new RecorridoFilter();
	public Long id;
	public Long recicladorId;
	public Long organizacionId;
	public Long zonaId;
	public LocalDate fechaRetiro;

	public RecorridoFilter(Map<String, List<String>> queryParams) {
		this.id = Parser.parseLong(queryParams, "id");
		this.recicladorId = Parser.parseLong(queryParams, "reciclador_id");
		this.organizacionId = Parser.parseLong(queryParams, "organizacion_id");
		this.zonaId = Parser.parseLong(queryParams, "zona_id");
		this.fechaRetiro = Parser.parseLocalDate(queryParams, "fecha_retiro");
	}

	public static RecorridoFilter byId(long id) { return builder().id(id).build(); }
}

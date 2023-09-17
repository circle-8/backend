package org.circle8.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.circle8.utils.Parser;

import java.time.LocalDate;
import java.time.ZonedDateTime;
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

	public InequalityFilter<LocalDate> fechaRetiro;
	public InequalityFilter<ZonedDateTime> fechaInicio;
	public InequalityFilter<ZonedDateTime> fechaFin;

	public RecorridoFilter(Map<String, List<String>> queryParams) {
		this.id = Parser.parseLong(queryParams, "id");
		this.recicladorId = Parser.parseLong(queryParams, "reciclador_id");
		this.organizacionId = Parser.parseLong(queryParams, "organizacion_id");
		this.zonaId = Parser.parseLong(queryParams, "zona_id");

		this.fechaRetiro = new InequalityFilter<>(queryParams, "fecha_retiro", Parser::parseLocalDate);
		this.fechaInicio = new InequalityFilter<>(queryParams, "fecha_inicio", Parser::parseLocalZonedDateTime);
		this.fechaFin = new InequalityFilter<>(queryParams, "fecha_fin", Parser::parseLocalZonedDateTime);
	}

	public static RecorridoFilter byId(long id) { return builder().id(id).build(); }
}

package org.circle8.filter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.circle8.utils.Parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SuscripcionFilter {
	public static final SuscripcionFilter EMPTY = new SuscripcionFilter();

	public Long id;
	public Long planId;
	public InequalityFilter<LocalDate> ultimaRenovacion;
	public InequalityFilter<LocalDate> proximaRenovacion;
	
	public SuscripcionFilter(Map<String, List<String>> queryParams) {
		this.id = Parser.parseLong(queryParams, "id");
		this.planId = Parser.parseLong(queryParams, "plan_id");
		this.ultimaRenovacion = new InequalityFilter<>(queryParams, "ultima_renovacion", Parser::parseLocalDate);
		this.proximaRenovacion = new InequalityFilter<>(queryParams, "proxima_renovacion", Parser::parseLocalDate);
	}

	public static SuscripcionFilter byId(long id) { return builder().id(id).build(); }
}

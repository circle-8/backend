package org.circle8.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.utils.Parser;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InequalityFilter<T> {
	public T equal;
	public T gt;
	public T ge;
	public T lt;
	public T le;
	public Boolean isNull;

	/**
	 * Parsea los query params para inecuaciones.
	 * Si el parametro es `fecha_retiro`, va a buscar todos los parametros posibles:
	 * - `gt`: greater than. Ejemplo, `fecha_retiro_gt`
	 * - `ge`: greater equal
	 * - `lt`: less than
	 * - `le`: less equal
	 * - `null`: is null (true o false)
	 */
	public InequalityFilter(
		Map<String, List<String>> queryParams,
		String paramName,
		BiFunction<Map<String, List<String>>, String, T> parser
	) {
		this.equal = parser.apply(queryParams, paramName);
		this.gt = parser.apply(queryParams, paramName+"_gt");
		this.ge = parser.apply(queryParams, paramName+"_ge");
		this.lt = parser.apply(queryParams, paramName+"_lt");
		this.le = parser.apply(queryParams, paramName+"_le");
		this.isNull = Parser.parseBoolean(queryParams, paramName+"_null");
	}
}

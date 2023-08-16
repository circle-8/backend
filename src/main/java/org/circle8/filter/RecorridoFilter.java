package org.circle8.filter;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class RecorridoFilter {
	public static final RecorridoFilter EMPTY = new RecorridoFilter(null);
	public final Long id;

	public static RecorridoFilter byId(long id) { return new RecorridoFilter(id); }
}

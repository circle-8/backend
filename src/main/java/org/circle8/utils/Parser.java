package org.circle8.utiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest.Validation;

import jakarta.annotation.Nullable;

public class Parser {
	
	@Nullable
	public static String parseString(Validation validation,Map<String, List<String>> queryParams, String paramName) {
		return queryParams.getOrDefault(paramName, List.of("")).get(0);
	}
	
	@Nullable
	public static Long parseLong(Validation validation,Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Long.parseLong(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	public static Double parseDouble(Validation validation,Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Double.parseDouble(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}
	
	@Nullable
	public static LocalDateTime parseLocalDateTime(Validation validation,Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? LocalDate.parse(param.get(0),DateTimeFormatter.ISO_LOCAL_DATE).atTime(0, 0, 0) : null;
		} catch ( DateTimeParseException e ) {
			validation.add(String.format("%s debe ser en formato ISO (2023-07-25)", paramName));
			return null;
		}		
	}
}

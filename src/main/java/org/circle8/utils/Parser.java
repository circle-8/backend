package org.circle8.utils;

import java.time.ZonedDateTime;
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
	public static ZonedDateTime parseLocalZonedDateTime(Validation validation,Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? ZonedDateTime.parse(param.get(0),DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
		} catch ( DateTimeParseException e ) {
			validation.add(String.format("%s debe ser en formato ISO_OFFSET_DATE_TIME (2023-07-19T23:13:14.445Z)", paramName));
			return null;
		}		
	}
}

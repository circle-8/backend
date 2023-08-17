package org.circle8.utils;

import jakarta.annotation.Nullable;
import org.circle8.controller.request.IRequest.Validation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class Parser {
	@Nullable
	public static Long parseLong(Validation validation, Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Long.parseLong(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			if ( validation != null )
				validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	public static Long parseLong(Map<String, List<String>> queryParams, String paramName) {
		return parseLong(null, queryParams, paramName);
	}

	@Nullable
	public static Double parseDouble(Validation validation, Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Double.parseDouble(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			validation.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	public static ZonedDateTime parseLocalZonedDateTime(Validation validation, Map<String, List<String>> queryParams, String paramName) {
		try {
			var param = queryParams.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? ZonedDateTime.parse(param.get(0),DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
		} catch ( DateTimeParseException e ) {
			if ( validation != null )
				validation.add(String.format("%s debe ser en formato ISO_OFFSET_DATE_TIME (2023-07-19T23:13:14.445Z)", paramName));
			return null;
		}
	}

	@Nullable
	public static ZonedDateTime parseLocalZonedDateTime(Map<String, List<String>> queryParams, String paramName) {
		return parseLocalZonedDateTime(null, queryParams, paramName);
	}

	public static List<Long> parseLongList(List<String> l) {
		try {
			return l.stream().map(Long::parseLong).toList();
		} catch ( NumberFormatException e ) {
			return List.of();
		}
	}
}

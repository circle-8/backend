package org.circle8.utils;

import jakarta.annotation.Nullable;
import org.circle8.controller.request.IRequest.Validation;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class Parser {
	@Nullable
	public static Long parseLong(Validation v, Map<String, List<String>> params, String paramName) {
		try {
			var param = params.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Long.parseLong(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			if ( v != null )
				v.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	public static Long parseLong(Map<String, List<String>> params, String paramName) {
		return parseLong(null, params, paramName);
	}

	@Nullable
	public static Double parseDouble(Validation v, Map<String, List<String>> params, String paramName) {
		try {
			var param = params.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? Double.parseDouble(param.get(0)) : null;
		} catch ( NumberFormatException e ) {
			v.add(String.format("%s debe ser un numero", paramName));
			return null;
		}
	}

	@Nullable
	public static ZonedDateTime parseLocalZonedDateTime(Validation v, Map<String, List<String>> params, String paramName) {
		try {
			var param = params.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? ZonedDateTime.parse(param.get(0),DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
		} catch ( DateTimeParseException e ) {
			if ( v != null )
				v.add(String.format("%s debe ser en formato ISO_OFFSET_DATE_TIME (2023-07-19T23:13:14.445Z)", paramName));
			return null;
		}
	}

	@Nullable
	public static ZonedDateTime parseLocalZonedDateTime(Map<String, List<String>> params, String paramName) {
		return parseLocalZonedDateTime(null, params, paramName);
	}

	public static LocalDate parseLocalDate(Validation v, Map<String, List<String>> params, String paramName) {
		try {
			var param = params.getOrDefault(paramName, List.of());
			return !param.isEmpty() ? LocalDate.parse(param.get(0),DateTimeFormatter.ISO_LOCAL_DATE) : null;
		} catch ( DateTimeParseException e ) {
			if ( v != null )
				v.add(String.format("%s debe ser en formato ISO_LOCAL_DATE (2023-07-19)", paramName));
			return null;
		}
	}

	@Nullable
	public static LocalDate parseLocalDate(Map<String, List<String>> params, String paramName) {
		return parseLocalDate(null, params, paramName);
	}

	public static List<Long> parseLongList(List<String> l) {
		try {
			return l.stream().map(Long::parseLong).toList();
		} catch ( NumberFormatException e ) {
			return List.of();
		}
	}
}

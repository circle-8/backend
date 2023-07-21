package org.circle8.dto;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public enum Dia {
	@SerializedName("0") LUNES,
	@SerializedName("1") MARTES,
	@SerializedName("2") MIERCOLES,
	@SerializedName("3") JUEVES,
	@SerializedName("4") VIERNES,
	@SerializedName("5") SABADO,
	@SerializedName("6") DOMINGO;

	private static final Gson GSON = new Gson();

	public static Dia get(int ordinal) {
		if ( ordinal > Dia.values().length || ordinal < 0 ) return null;
		return Dia.values()[ordinal];
	}

	/**
	 * Parsea el string de dias y devuelve el listado en base a los que esten
	 * marcados como 1
	 * @param rs es del tipo [1, 0, 1, 0, 1, 1, 1]
	 */
	public static List<Dia> getDia(String rs) {
		Integer[] days = GSON.fromJson(rs, Integer[].class);
		return IntStream.range(0, days.length)
			.filter(i -> days[i].equals(1))
			.mapToObj(Dia::get)
			.toList();
	}

	public static String getDias(List<Dia> list) {
		int[] diasArray = new int[7];

		list.forEach(dia -> diasArray[dia.ordinal()] = 1);

		return GSON.toJson(diasArray);
	}

	/**
	 * Transforma una lista de Integers en una lista de dias usando la función interna
	 * @param ld
	 * @return List<Dia>
	 */
	public static List<Dia> getDia(List<Integer> ld) {
		return ld.stream().map(Dia::get).toList();
	}


}

package org.circle8.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public enum Dia {
	@SerializedName("0")
	LUNES, @SerializedName("1")
	MARTES, @SerializedName("2")
	MIERCOLES, @SerializedName("3")
	JUEVES, @SerializedName("4")
	VIERNES, @SerializedName("5")
	SABADO, @SerializedName("6")
	DOMINGO;

	public static Dia get(int ordinal) {
		return switch (ordinal) {
		case 0 -> LUNES;
		case 1 -> MARTES;
		case 2 -> MIERCOLES;
		case 3 -> JUEVES;
		case 4 -> VIERNES;
		case 5 -> SABADO;
		case 6 -> DOMINGO;
		default -> null;
		};
	}

	/**
	 * Parsea el string de dias y devuelve el listado en base a los que esten
	 * marcados como 1
	 * 
	 * @param rs
	 * @return
	 */
	public static List<Dia> getDia(String rs) {
		var result = rs.replace("[", "").replace("]", "");
		var listDias = new ArrayList<Dia>();
		if (result.contains("1")) {
			var dias = result.split(",");
			for (int i = 0; i < dias.length; i++) {
				if (dias[i].trim().equals("1")) {
					listDias.add(Dia.get(i));
				}
			}
		}
		return listDias;
	}
}

package org.circle8.dto;

import com.google.gson.annotations.SerializedName;

public enum Dia {
	@SerializedName("0")
	LUNES,
	@SerializedName("1")
	MARTES,
	@SerializedName("2")
	MIERCOLES,
	@SerializedName("3")
	JUEVES,
	@SerializedName("4")
	VIERNES,
	@SerializedName("5")
	SABADO,
	@SerializedName("6")
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
}

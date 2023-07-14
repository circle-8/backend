package org.circle8.controller.response;

import org.circle8.dto.Dia;

import com.google.gson.annotations.SerializedName;

public enum DiaResponse {
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
	
	public static DiaResponse from(Dia dia) {
		return switch (dia) {
		case LUNES -> LUNES;
		case MARTES -> MARTES;
		case MIERCOLES -> MIERCOLES;
		case JUEVES -> JUEVES;
		case VIERNES -> VIERNES;
		case SABADO -> SABADO;
		case DOMINGO -> DOMINGO;
		};
	}
	
	public Dia to() {
		return switch(this) {
		case LUNES -> Dia.LUNES;
		case MARTES -> Dia.MARTES;
		case MIERCOLES -> Dia.MIERCOLES;
		case JUEVES -> Dia.JUEVES;
		case VIERNES -> Dia.VIERNES;
		case SABADO -> Dia.SABADO;
		case DOMINGO -> Dia.DOMINGO;
		};
	}
}

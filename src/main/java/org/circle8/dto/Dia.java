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
}

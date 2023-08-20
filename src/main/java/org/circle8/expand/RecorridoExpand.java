package org.circle8.expand;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RecorridoExpand {
	public static final RecorridoExpand EMPTY = new RecorridoExpand(false, false, false);

	public final boolean zona;
	public final boolean reciclador;
	public final boolean residuos;

	public RecorridoExpand(List<String> expands) {
		this.zona = expands.contains("zona");
		this.reciclador = expands.contains("reciclador");
		this.residuos = true; // Cuando se trata de un request por GET (que tiene expands), siempre devolvemos residuos
	}
}

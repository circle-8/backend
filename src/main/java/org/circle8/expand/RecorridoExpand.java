package org.circle8.expand;


import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class RecorridoExpand {
	public static final RecorridoExpand EMPTY = new RecorridoExpand(false, false, false);
	public static final RecorridoExpand ALL = new RecorridoExpand(true, true, true);

	public final boolean zona;
	public final boolean reciclador;
	public final boolean residuos;

	public RecorridoExpand(List<String> expands) {
		this.zona = expands.contains("zona");
		this.reciclador = expands.contains("reciclador");
		this.residuos = true; // Cuando se trata de un request por GET (que tiene expands), siempre devolvemos residuos
	}
}

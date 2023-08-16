package org.circle8.expand;

import java.util.List;

import lombok.ToString;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@ToString
public class PuntoResiduoExpand {
	public static final PuntoResiduoExpand EMPTY = new PuntoResiduoExpand(false, false);
	public final boolean ciudadano;
	public final boolean residuos;

	public PuntoResiduoExpand(List<String> expands) {
		this.ciudadano = expands.contains("ciudadano");
		this.residuos = expands.contains("residuos"); // TODO: not implemented for list
	}
}

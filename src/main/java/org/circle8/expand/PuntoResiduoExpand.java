package org.circle8.expand;

import java.util.List;

import lombok.ToString;

@ToString
public class PuntoResiduoExpand {
	public final boolean ciudadano;
	public final boolean residuos;

	public PuntoResiduoExpand(List<String> expands) {
		this.ciudadano = expands.contains("ciudadano");
		this.residuos = expands.contains("residuos"); // TODO: not implemented for list
	}
}

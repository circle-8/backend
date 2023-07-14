package org.circle8.expand;

import java.util.List;

public class PuntoResiduoExpand {
	public boolean ciudadano;
	public boolean residuos;

	public PuntoResiduoExpand(List<String> expands) {
		this.ciudadano = expands.contains("ciudadano");
		this.residuos = expands.contains("residuos"); // TODO: not implemented for list
	}
}

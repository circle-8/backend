package org.circle8.expand;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
public class PuntoResiduoExpand {
	public static final PuntoResiduoExpand EMPTY = new PuntoResiduoExpand(false, false, false);
	public final boolean ciudadano;
	public final boolean residuos;
	public final boolean residuosBase64;

	public PuntoResiduoExpand(List<String> expands) {
		this.ciudadano = expands.contains("ciudadano");

		// TODO: not implemented for list
		this.residuos = expands.contains("residuos");
		this.residuosBase64 = expands.contains("residuos.base64");
	}
}

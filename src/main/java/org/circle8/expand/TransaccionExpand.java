package org.circle8.expand;

import java.util.List;

import lombok.ToString;

@ToString
public class TransaccionExpand {
	public final boolean puntoReciclaje;
	public final boolean transporte;
	public final boolean residuos;

	public TransaccionExpand(List<String> expands) {
		this.puntoReciclaje = expands.contains("punto_reciclaje");
		this.transporte = expands.contains("transporte");
		this.residuos = expands.contains("residuos");
	}
}

package org.circle8.expand;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
public class TransaccionExpand {
	public static final TransaccionExpand EMPTY = new TransaccionExpand(false, false,false);
	public final boolean puntoReciclaje;
	public final boolean transporte;
	public final boolean residuos;

	public TransaccionExpand(List<String> expands) {
		this.puntoReciclaje = expands.contains("punto_reciclaje");
		this.transporte = expands.contains("transporte");
		this.residuos = expands.contains("residuos");
	}
}

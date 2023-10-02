package org.circle8.expand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Builder
@ToString
public class TransaccionExpand {
	public static final TransaccionExpand EMPTY = new TransaccionExpand(false, false, false, false);
	public final boolean puntoReciclaje;
	public final boolean transporte;
	public final boolean residuos;
	public final boolean residuosBase64;

	public TransaccionExpand(List<String> expands) {
		this.puntoReciclaje = expands.contains("punto_reciclaje");
		this.transporte = expands.contains("transporte");
		this.residuos = expands.contains("residuos");
		this.residuosBase64 = expands.contains("residuos.base64");
	}
}

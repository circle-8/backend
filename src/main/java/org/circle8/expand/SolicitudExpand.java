package org.circle8.expand;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
public class SolicitudExpand {
	public static final SolicitudExpand EMPTY = new SolicitudExpand(false, false, false);
	public final boolean ciudadanos;
	public final boolean residuo;
	public final boolean puntoReciclaje;

	public SolicitudExpand(List<String> expands) {
		this.ciudadanos = expands.contains("ciudadanos");
		this.residuo = expands.contains("residuo");
		this.puntoReciclaje = expands.contains("punto_reciclaje");
	}
}

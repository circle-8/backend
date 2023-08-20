package org.circle8.expand;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;


@AllArgsConstructor
@Builder
@ToString
public class ZonaExpand {
	public static final ZonaExpand EMPTY = new ZonaExpand(false, false, false);
	public boolean organizacion;
	public boolean recorridos;
	public boolean puntosResiduo;

	public ZonaExpand(List<String> expands) {
		this.organizacion = expands.contains("organizacion");
		this.recorridos = expands.contains("recorridos");
		this.puntosResiduo = expands.contains("punto_residuo");
	}
}

package org.circle8.expand;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ZonaExpand {
	public static final ZonaExpand EMPTY = new ZonaExpand(false, false);
	public boolean organizacion;
	public boolean recorridos;

	public ZonaExpand(List<String> expands) {
		this.organizacion = expands.contains("organizacion");
		this.recorridos = expands.contains("recorridos");
	}
}

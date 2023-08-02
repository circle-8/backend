package org.circle8.expand;

import lombok.ToString;

import java.util.List;

@ToString
public class ZonaExpand {
	public boolean organizacion;
	public boolean recorridos;

	public ZonaExpand(List<String> expands) {
		this.organizacion = expands.contains("organizacion");
		this.recorridos = expands.contains("recorridos");
	}
}

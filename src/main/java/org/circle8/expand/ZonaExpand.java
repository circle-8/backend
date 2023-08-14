package org.circle8.expand;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@ToString
public class ZonaExpand {
	public boolean organizacion;
	public boolean recorridos;

	public ZonaExpand(List<String> expands) {
		this.organizacion = expands.contains("organizacion");
		this.recorridos = expands.contains("recorridos");
	}
}

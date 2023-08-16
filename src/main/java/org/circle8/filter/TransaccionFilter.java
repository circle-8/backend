package org.circle8.filter;

import java.util.List;

import lombok.Builder;

@Builder
public class TransaccionFilter {

	public Long id;
	public Long transportistaId;
	public List<Long> puntosReciclaje;

	public boolean hasPuntos() {
		return puntosReciclaje != null && !puntosReciclaje.isEmpty();
	}
}

package org.circle8.filter;

import lombok.Builder;

import java.util.List;

@Builder
public class TransaccionFilter {

	public Long id;
	public Long transportistaId;
	public List<Long> puntosReciclaje;

	public boolean hasPuntos() {
		return puntosReciclaje != null && !puntosReciclaje.isEmpty();
	}
}

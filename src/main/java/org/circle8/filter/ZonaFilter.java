package org.circle8.filter;

import java.util.List;

import lombok.Builder;

@Builder
public class ZonaFilter {
	public Long id;
	public Long organizacionId;
	public Long recicladorId;
	public Long ciudadanoId;
	public Long puntoResiduoId;
	public List<Integer> tiposResiduos;
	
	public boolean hasTipo() {
		return tiposResiduos != null && !tiposResiduos.isEmpty();
	}
}

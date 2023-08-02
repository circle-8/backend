package org.circle8.filter;

import java.util.List;

import lombok.Builder;

@Builder
public class ZonaFilter {
	public Long id;
	public Long organizacionId;	
	public List<Integer> tiposResiduos;
	
	public boolean hasTipo() {
		return !tiposResiduos.isEmpty();
	}
}

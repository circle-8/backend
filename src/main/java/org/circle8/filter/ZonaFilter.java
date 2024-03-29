package org.circle8.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

	public ZonaFilter(long id) { this.id = id; }
}

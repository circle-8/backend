package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuntoResiduo {
	public Long id;
	public Double latitud;
	public Double longitud;
	public Long ciudadanoId;
	public User ciudadano;
	public List<Residuo> residuos = List.of();

	public PuntoResiduo(Long id) { this.id = id; }
	public PuntoResiduo(Long id, Long ciudadanoId) { this.id = id; this.ciudadanoId = ciudadanoId; }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PuntoResiduo other = (PuntoResiduo) obj;
		return Objects.equals(id, other.id);
	}
}

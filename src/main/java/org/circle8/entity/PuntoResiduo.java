package org.circle8.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuntoResiduo {
	public long id;
	public Double latitud;
	public Double longitud;
	public Long ciudadanoId;
	public User ciudadano;
	public List<Residuo> residuos = List.of();

	public PuntoResiduo(Long id) { this.id = id; }
	public PuntoResiduo(Long id, Long ciudadanoId) { this.id = id; this.ciudadanoId = ciudadanoId; }
}

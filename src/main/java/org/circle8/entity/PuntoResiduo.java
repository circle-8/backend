package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

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
}

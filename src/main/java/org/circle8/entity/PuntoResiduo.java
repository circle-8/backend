package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuntoResiduo {
	public long id;
	public double latitud;
	public double longitud;
	public long ciudadanoId;
	public User ciudadano;
}

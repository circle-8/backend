package org.circle8.entity;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public class Residuo {
	public long id;
	public long ciudadanoId;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaCreacion;
	public PuntoResiduo punto;
	public TipoResiduo tipo;
	public Recorrido recorrido;
	public Transaccion transaccion;
}

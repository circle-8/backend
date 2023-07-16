package org.circle8.entity;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class Residuo {
	public long id;
	public long ciudadanoId;
	public LocalDateTime fechaRetiro;
	public LocalDateTime fechaCreacion;
	public PuntoResiduo punto;
	public TipoResiduo tipo;
	public Recorrido recorrido;
	public Transaccion transaccion;
}

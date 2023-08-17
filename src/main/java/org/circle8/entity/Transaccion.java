package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaccion {
	public long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;
	public Long transporteId;
	public Transporte transporte;
	public Long puntoReciclajeId;
	public PuntoReciclaje puntoReciclaje;
	public List<Residuo> residuos;

	public Transaccion(long id) { this.id = id; }
}

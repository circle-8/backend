package org.circle8.entity;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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

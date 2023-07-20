package org.circle8.entity;

import java.time.ZonedDateTime;
import java.util.List;

public class Transaccion {
	public long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;
	public Long transporteId;
	public Transporte transporte;
	public Long puntoReciclajeId;
	public PuntoReciclaje puntoReciclaje;
	public List<Residuo> residuos;
}

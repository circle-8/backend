package org.circle8.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Transaccion {
	public long id;
	public LocalDateTime fechaCreacion;
	public LocalDateTime fechaRetiro;
	public Long transporteId;
	public Transporte transporte;
	public Long puntoReciclajeId;
	public PuntoReciclaje puntoReciclaje;
	public List<Residuo> residuos;
}

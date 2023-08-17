package org.circle8.entity;

import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
public class Solicitud {
	public long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaModificacion;
	public EstadoSolicitud estado;
	public Ciudadano solicitante;
	public Ciudadano solicitado;
	public Residuo residuo;
	public Long canceladorId;
	public PuntoReciclaje puntoReciclaje;
}

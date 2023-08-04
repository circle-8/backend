package org.circle8.entity;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;

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
}

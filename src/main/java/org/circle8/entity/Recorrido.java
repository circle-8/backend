package org.circle8.entity;


import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recorrido {
	public long id;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public Long recicladorId;
	public Ciudadano reciclador;
	public Long zonaId;
	public Zona zona;
	public Punto puntoInicio;
	public Punto puntoFin;
	public List<Retiro> puntos;
}

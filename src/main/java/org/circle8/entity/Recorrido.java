package org.circle8.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recorrido {
	public Long id;
	public LocalDateTime fechaRetiro;
	public LocalDateTime fechaInicio;
	public LocalDateTime fechaFin;
	public String recicladorUri;
	public Long recicladorId;
	public Ciudadano reciclador;
	public String zonaUri;
	public Long zonaId;
	public Zona zona;
	public Punto puntoInicio;
	public Punto puntoFin;
	public List<Retiro> puntos;
}

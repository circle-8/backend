package org.circle8.entity;


import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recorrido {
	public long id;
	public LocalDate fechaRetiro;
	public ZonedDateTime fechaInicio;
	public ZonedDateTime fechaFin;
	public Long recicladorId;
	public Ciudadano reciclador;
	public Long zonaId;
	public Long organizacionId; //requerido para la uri de zona
	public Zona zona;
	public Punto puntoInicio;
	public Punto puntoFin;
	public List<Retiro> puntos;
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recorrido other = (Recorrido) obj;
		return id == other.id;
	}
}



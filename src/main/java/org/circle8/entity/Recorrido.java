package org.circle8.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
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

	public Recorrido(long id ) { this.id = id; }

	@Override public int hashCode() { return Objects.hash(id); }
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Recorrido other = (Recorrido) obj;
		return id == other.id;
	}

	@NotNull
	public List<Residuo> getResiduos() {
		if ( puntos == null ) return List.of();
		return puntos.stream().map(p -> p.residuo).toList();
	}
}

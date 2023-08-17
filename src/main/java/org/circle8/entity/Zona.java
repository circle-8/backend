package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Zona {
	public Long id;
	public String nombre;
	public List<Punto> polyline;
	public Long organizacionId;
	public Organizacion organizacion;
	public List<TipoResiduo> tipoResiduo;
	public List<Recorrido> recorridos;
	public List<PuntoResiduo> puntosResiduos;

	public Zona(long id ) { this.id = id; }
}

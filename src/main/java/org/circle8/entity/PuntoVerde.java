package org.circle8.entity;

import java.util.List;

import org.circle8.dto.Dia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuntoVerde {
	public long id;
	public String titulo;
	public double latitud;
	public double longitud;
	public List<Dia> dias;
	public List<TipoResiduo> tipoResiduo;
}

package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.dto.Dia;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuntoReciclaje {
	public long id;
	public String titulo;
	public double latitud;
	public double longitud;
	public List<Dia> dias;
	public List<TipoResiduo> tipoResiduo;
	public Long recicladorId;
	public User reciclador;

	public PuntoReciclaje(long id, long recicladorId) {
		this.id = id;
		this.recicladorId = recicladorId;
	}
}

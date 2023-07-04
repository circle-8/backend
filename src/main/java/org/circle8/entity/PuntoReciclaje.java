package org.circle8.entity;

import java.util.List;

import org.circle8.dto.Dia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PuntoReciclaje {
	public int id;
	public float latitud;
	public float longitud;
	public List<Dia> dias;
	public List<TipoResiduo> tipoResiduo;
	public String recicladorUri;
	public Integer recicladorId;
	public Ciudadano reciclador;
}

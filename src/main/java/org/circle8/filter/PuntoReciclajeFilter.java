package org.circle8.filter;

import lombok.Builder;
import org.circle8.dto.Dia;

import java.util.List;

@Builder
public class PuntoReciclajeFilter {

	public List<Dia> dias;
	public List<Integer> tiposResiduos;
	public Long recicladorId;
	public Long notRecicladorId;
	public Double latitud;
	public Double longitud;
	public Double radio;
	public boolean isPuntoVerde;

	public boolean hasDias() {
		return dias != null && !dias.isEmpty();
	}

	public boolean hasReciclador() {
		return recicladorId != null;
	}

	public boolean hasTipo() {
		return tiposResiduos != null && !tiposResiduos.isEmpty();
	}

	public boolean hasArea() {
		return latitud != null && longitud != null && radio != null;
	}

	public boolean isPuntoVerde() {
		return isPuntoVerde;
	}

}

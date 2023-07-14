package org.circle8.filter;

import java.util.List;

import lombok.Builder;
import org.circle8.dto.Dia;

@Builder
public class PuntoVerdeFilter {

	public List<Dia> dias;
	public List<String> tiposResiduos;
	public Double latitud;
	public Double longitud;
	public Double radio;

	public boolean hasDias() {
		return dias != null && !dias.isEmpty();
	}

	public boolean hasTipo() {
		return tiposResiduos != null && !tiposResiduos.isEmpty();
	}

	public boolean hasArea() {
		return latitud != null && longitud != null && radio != null;
	}
}

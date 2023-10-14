package org.circle8.filter;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public class TransaccionFilter {

	public Long id;
	public Long transportistaId;
	public Long ciudadanoId;
	public Boolean conTransporte;
	public List<Long> puntosReciclaje;
	public InequalityFilter<ZonedDateTime> fechaRetiro; // TODO: falta agregarlo en el controller y cambiarlo en la APP

	public boolean hasPuntos() {
		return puntosReciclaje != null && !puntosReciclaje.isEmpty();
	}
}

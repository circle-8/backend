package org.circle8.filter;

import lombok.Builder;

@Builder
public class PuntoReciclajeFilter {
	
	public String dias;	
	public String tipoResiduo;
	public Long reciclador_id;
	public Double latitud;
	public Double longitud;
	public Double radio;
	
	public boolean hasDias() {
		return dias != null;
	}
	
	public boolean hasReciclador() {
		return reciclador_id != null;
	}
	
	public boolean hasTipo() {
		return tipoResiduo != null;
	}
	
	public boolean hasArea() {
		return latitud != null && longitud != null && radio != null;
	}		
}

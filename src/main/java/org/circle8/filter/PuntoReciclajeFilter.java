package org.circle8.filter;

import java.util.List;

import lombok.Builder;

@Builder
public class PuntoReciclajeFilter {
	
	public List<String> dias;
	public List<String> tipoResiduo;
	public Long reciclador_id;
	public Double latitud;
	public Double longitud;
	public Double radio;
	
	public boolean hasDias() {
		return !dias.isEmpty();
	}
	
	public boolean hasReciclador() {
		return reciclador_id != null;
	}
	
	public boolean hasTipo() {
		return !tipoResiduo.isEmpty();
	}
	
	public boolean hasArea() {
		return latitud != null && longitud != null && radio != null;
	}		
}

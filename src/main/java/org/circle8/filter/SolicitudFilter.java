package org.circle8.filter;

import lombok.Builder;

@Builder
public class SolicitudFilter {	
	public Long solicitanteId;
	public Long solicitadoId;
	
	public boolean hasSolicitante() {
		return solicitanteId != null;
	}
	
	public boolean hasSolicitado() {
		return solicitadoId != null;
	}
}

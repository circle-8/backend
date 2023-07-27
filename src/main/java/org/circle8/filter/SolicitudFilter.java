package org.circle8.filter;

import lombok.Builder;

@Builder
public class SolicitudFilter {
	public Long id;
	public Long solicitanteId;
	public Long solicitadoId;
}

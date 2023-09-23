package org.circle8.filter;

import lombok.Builder;
import org.circle8.entity.EstadoSolicitud;

import java.util.List;

@Builder
public class SolicitudFilter {
	public Long id;
	public Long solicitanteId;
	public Long solicitadoId;
	public Long residuoId;
	public Long puntoReciclajeId;
	public List<EstadoSolicitud> notEstados;
	public List<EstadoSolicitud> estados;
}

package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.entity.EstadoSolicitud;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SolicitudResponse implements ApiResponse {
	public long id;
	public Long solicitanteId; // ID de ciudadano, NO de user
	public String solicitanteUri;
	public CiudadanoResponse solicitante;
	public Long solicitadoId; // ID de ciudadano, NO de user
	public String solicitadoUri;
	public CiudadanoResponse solicitado;
	public EstadoSolicitud estado;
	public Long canceladorId;
	public ResiduoResponse residuo;
	public Long puntoReciclajeId;
	public String puntoReciclajeUri;
	public PuntoReciclajeResponse puntoReciclaje;
}

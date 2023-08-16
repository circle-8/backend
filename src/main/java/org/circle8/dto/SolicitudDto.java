package org.circle8.dto;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.circle8.controller.response.SolicitudResponse;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.entity.Solicitud;

import lombok.val;

public class SolicitudDto {
	public long id;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaModificacion;
	public EstadoSolicitud estado;
	public CiudadanoDto solicitante;
	public CiudadanoDto solicitado;
	public ResiduoDto residuo;
	public Long canceladorId;
	public PuntoReciclajeDto puntoReciclaje;

	public static SolicitudDto from(Solicitud entity) {
		if ( entity == null ) return null;
		val s = new SolicitudDto();
		s.id = entity.id;
		s.fechaCreacion = entity.fechaCreacion;
		s.fechaModificacion = entity.fechaModificacion;
		s.estado = entity.estado;
		s.solicitante = CiudadanoDto.from(entity.solicitante);
		s.solicitado = CiudadanoDto.from(entity.solicitado);
		s.residuo = ResiduoDto.from(entity.residuo);
		s.canceladorId = entity.canceladorId;
		s.puntoReciclaje = PuntoReciclajeDto.from(entity.puntoReciclaje);
		return s;
	}

	public SolicitudResponse toResponse() {
		return new SolicitudResponse(
			id,
			solicitante.id,
			"/user/" + solicitante.usuarioId,
			solicitante.toResponse(),
			solicitado.id,
			"/user/" + solicitado.usuarioId,
			solicitado.toResponse(),
			estado,
			!Objects.equals(canceladorId, 0L) ? canceladorId : null,
			residuo.toResponse(),
			puntoReciclaje != null ? puntoReciclaje.id : null,
			puntoReciclaje != null ? "/reciclador/" + puntoReciclaje.recicladorId + "/punto_reciclaje/" + puntoReciclaje.id : null,
			puntoReciclaje != null ? puntoReciclaje.toResponse() : null
		);
	}
}

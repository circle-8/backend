package org.circle8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.circle8.controller.request.consejo.PostConsejoRequest;
import org.circle8.controller.response.ConsejoResponse;
import org.circle8.entity.Consejo;

import java.time.LocalDate;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ConsejoDto {
	public Long id;
	public final String titulo;
	public final String descripcion;
	public final LocalDate fechaCreacion;

	public static ConsejoDto from(PostConsejoRequest req) {
		if ( req == null ) return null;
		return new ConsejoDto(req.titulo, req.descripcion, LocalDate.now());
	}

	public static ConsejoDto from(Consejo entity) {
		if ( entity == null ) return null;
		return new ConsejoDto(entity.id, entity.titulo, entity.descripcion, entity.fechaCreacion);
	}

	public Consejo toEntity() {
		return new Consejo(id, titulo, descripcion, fechaCreacion);
	}

	public ConsejoResponse toResponse() {
		return new ConsejoResponse(id, titulo, descripcion, fechaCreacion);
	}
}

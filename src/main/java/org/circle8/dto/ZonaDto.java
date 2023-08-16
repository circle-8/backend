package org.circle8.dto;

import java.util.List;

import org.circle8.controller.request.zona.PostPutZonaRequest;
import org.circle8.controller.response.ZonaResponse;
import org.circle8.entity.Zona;

public class ZonaDto {
	public long id;
	public String nombre;
	public List<PuntoDto> polyline;
	public Long organizacionId;
	public OrganizacionDto organizacion;
	public List<TipoResiduoDto> tipoResiduo;
	public List<RecorridoDto> recorridos;
	public List<PuntoResiduoDto> puntosResiduos;

	
	public static ZonaDto from(PostPutZonaRequest req) {
		var dto = new ZonaDto();
		dto.nombre = req.nombre;
		dto.polyline = req.polyline.stream().map(PuntoDto::from).toList();
		dto.tipoResiduo = req.tiposResiduo.stream().map(TipoResiduoDto::from).toList();
		return dto;
	}	

	public static ZonaDto from(Zona entity) {
		if ( entity == null ) return null;

		var z = new ZonaDto();
		z.id = entity.id;
		z.nombre = entity.nombre;

		z.polyline = entity.polyline != null
			? entity.polyline.stream().map(PuntoDto::from).toList()
			: List.of();

		z.organizacionId = entity.organizacionId;
		z.organizacion = OrganizacionDto.from(entity.organizacion);

		z.tipoResiduo = entity.tipoResiduo != null
			? entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList()
			: List.of();

		z.recorridos = entity.recorridos != null
			? entity.recorridos.stream().map(RecorridoDto::from).toList()
			: List.of();

		z.puntosResiduos = entity.puntosResiduos != null
			? entity.puntosResiduos.stream().map(PuntoResiduoDto::from).toList()
			: List.of();

		return z;
	}

	public ZonaResponse toResponse() {
		var zr = new ZonaResponse();
		zr.id = this.id;
		zr.nombre = this.nombre;
		zr.polyline = this.polyline.stream().map(PuntoDto::toResponse).toList();
		zr.organizacionUri = "/organizacion/" + this.organizacionId;
		zr.organizacionId = this.organizacionId;
		zr.organizacion = this.organizacion != null ? this.organizacion.toResponse() : null;
		zr.tipoResiduo = this.tipoResiduo.stream().map(TipoResiduoDto::toResponse).toList();
		zr.recorridos = (this.recorridos != null && !this.recorridos.isEmpty()) ?
				this.recorridos.stream().map(RecorridoDto::toResponse).toList() : null;
		zr.puntosResiduos = (this.puntosResiduos != null && !this.puntosResiduos.isEmpty()) ?
				this.puntosResiduos.stream().map(PuntoResiduoDto::toResponse).toList() : null;
		return zr;
	}
	
	public Zona toEntity() {
		Zona z = new Zona();
		z.id = this.id;
		z.nombre = this.nombre;
		z.polyline = this.polyline != null ?
				this.polyline.stream().map(PuntoDto::toEntity).toList() : null;
		z.tipoResiduo = this.tipoResiduo != null ? 
				this.tipoResiduo.stream().map(TipoResiduoDto::toEntity).toList() : null;		
		return z;
	}
}

package org.circle8.dto;

import java.util.List;

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
	
	public static ZonaDto from(Zona entity) {
		if ( entity == null ) return null;
		var z = new ZonaDto();
		z.id = entity.id;
		z.nombre = entity.nombre;
		z.polyline = entity.polyline.stream().map(PuntoDto::from).toList();
		z.organizacionId = entity.organizacionId;
		z.organizacion = OrganizacionDto.from(entity.organizacion);
		z.tipoResiduo = entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList();
		z.recorridos = entity.recorridos.stream().map(RecorridoDto::from).toList();
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
		zr.recorridos = this.recorridos.stream().map(RecorridoDto::toResponse).toList();
		return zr;		
	}
}

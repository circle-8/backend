package org.circle8.dto;

import java.util.List;

import org.circle8.entity.Zona;

public class ZonaDto {
	public Long id;
	public String nombre;
	public List<PuntoDto> polyline;
	public Long organizacionId;
	public OrganizacionDto organizacion;
	public List<TipoResiduoDto> tipoResiduo;

	public static ZonaDto from(Zona entity) {
		if ( entity == null ) return null;
		var z = new ZonaDto();
		z.id = entity.id;
		z.nombre = entity.nombre;
		z.polyline = entity.polyline.stream().map(PuntoDto::from).toList();
		z.organizacionId = entity.organizacionId;
		z.organizacion = OrganizacionDto.from(entity.organizacion);
		z.tipoResiduo = entity.tipoResiduo.stream().map(TipoResiduoDto::from).toList();
		return z;
	}
}

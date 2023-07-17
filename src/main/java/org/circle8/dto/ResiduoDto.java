package org.circle8.dto;

import org.circle8.controller.response.ResiduoResponse;
import org.circle8.entity.Residuo;

import java.time.ZonedDateTime;

public class ResiduoDto {
	public long id;
	public long ciudadanoId;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaCreacion;
	public PuntoResiduoDto punto;
	public TipoResiduoDto tipo;
	public RecorridoDto recorrido;
	public TransaccionDto transaccion;

	public static ResiduoDto from(Residuo entity) {
		var r = new ResiduoDto();
		r.id = entity.id;
		r.ciudadanoId = entity.ciudadanoId;
		r.fechaRetiro = entity.fechaRetiro;
		r.fechaCreacion = entity.fechaCreacion;
		r.tipo = TipoResiduoDto.from(entity.tipo);
		r.punto = PuntoResiduoDto.from(entity.punto);
		r.recorrido = RecorridoDto.from(entity.recorrido);
		r.transaccion = TransaccionDto.from(entity.transaccion);
		return r;
	}

	public ResiduoResponse toResponse() {
		return new ResiduoResponse(
			id,
			fechaRetiro,
			fechaCreacion,
			"/ciudadano/"+ciudadanoId+"/punto_residuo/"+punto.id,
			punto.id,
			punto.toResponse(),
			tipo.toResponse(),
			recorrido != null ? "/recorrido/" + recorrido.id : null,
			recorrido != null ? recorrido.id : null,
			recorrido != null ? recorrido.toResponse() : null,
			transaccion != null ? "/transaccion/" + transaccion.id : null,
			transaccion != null ? transaccion.id : null,
			transaccion != null ? transaccion.toResponse() : null
		);
	}
}

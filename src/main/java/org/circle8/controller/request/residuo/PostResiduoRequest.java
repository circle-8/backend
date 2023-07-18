package org.circle8.controller.request.residuo;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

public class PostResiduoRequest implements IRequest {
	private final Validation validation = new Validation();
	
	public Long tipoResiduoId;
	public Long puntoResiduoId;
	public Long ciudadanoId;
	public ZonedDateTime fechaLimite;
	public String descripcion;
	
	public PostResiduoRequest(Map<String, List<String>> queryParams) {
		this.tipoResiduoId = Parser.parseLong(validation, queryParams, "tipo_residuo_id");
		this.puntoResiduoId = Parser.parseLong(validation, queryParams, "punto_residuo_id");
		this.ciudadanoId = Parser.parseLong(validation, queryParams, "ciudadano_id");
		this.fechaLimite = Parser.parseLocalZonedDateTime(validation, queryParams, "fecha_limite_retiro");
		this.descripcion = Parser.parseString(validation, queryParams, "descripcion");
	}


	@Override
	public Validation valid() {
		if(tipoResiduoId == null)
			validation.add("Se debe especificar el tipo de residuo");
		if(puntoResiduoId == null)
			validation.add("Se debe especificar el punto de reciduo");
		if(ciudadanoId == null)
			validation.add("Se debe especificar el id del ciudadano");
		if(fechaLimite != null && fechaLimite.isBefore(ZonedDateTime.now()))
			validation.add("La fecha limite de retiro no puede ser inferior a la fecha actual");
		return validation;
	}

}

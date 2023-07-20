package org.circle8.controller.request.residuo;

import java.time.ZonedDateTime;

import org.circle8.controller.request.IRequest;
import org.circle8.utils.Dates;

public class PostResiduoRequest implements IRequest {
	private final Validation validation = new Validation();
	
	public Long tipo_residuo_id;
	public Long punto_residuo_id;
	public Long ciudadano_id;
	public ZonedDateTime fecha_limite_retiro;
	public String descripcion;

	@Override
	public Validation valid() {
		if(tipo_residuo_id == null)
			validation.add("Se debe especificar el tipo de residuo");
		if(punto_residuo_id == null)
			validation.add("Se debe especificar el punto de reciduo");
		if(ciudadano_id == null)
			validation.add("Se debe especificar el id del ciudadano");
		if(descripcion == null || descripcion.isEmpty())
			validation.add("Se debe especificar la descripcion");
		if(fecha_limite_retiro != null && fecha_limite_retiro.isBefore(ZonedDateTime.now(Dates.UTC)))
			validation.add("La fecha limite de retiro no puede ser inferior a la fecha actual");
		return validation;
	}

}

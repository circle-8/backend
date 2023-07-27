package org.circle8.controller.request.residuo;

import java.time.ZonedDateTime;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Dates;

@ToString
public class PostResiduoRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long tipoResiduoId;
	public Long puntoResiduoId;
	public Long ciudadanoId;
	public ZonedDateTime fechaLimite;
	public String descripcion;

	@Override
	public Validation valid() {
		if(tipoResiduoId == null)
			validation.add("Se debe especificar el tipo de residuo");
		if(puntoResiduoId == null)
			validation.add("Se debe especificar el punto de reciduo");
		if(ciudadanoId == null)
			validation.add("Se debe especificar el id del ciudadano");
		if(descripcion == null || descripcion.isEmpty())
			validation.add("Se debe especificar la descripcion");
		if(fechaLimite != null && fechaLimite.isBefore(ZonedDateTime.now(Dates.UTC)))
			validation.add("La fecha limite de retiro no puede ser inferior a la fecha actual");
		return validation;
	}

}

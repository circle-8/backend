package org.circle8.controller.request.residuo;

import lombok.ToString;
import lombok.val;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;

@ToString
public class PostResiduoRequest implements IRequest {

	public Long tipoResiduoId;
	public Long puntoResiduoId;
	public Long ciudadanoId;
	public ZonedDateTime fechaLimite;
	public String descripcion;
	public String base64;

	@Override
	public Validation valid() {
		val validation = new Validation();
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

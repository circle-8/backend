package org.circle8.controller.request.residuo;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;

@ToString
public class PutResiduoRequest implements IRequest {

	public Long id;
	public Long tipoResiduoId;
	public ZonedDateTime fechaLimite;
	public String descripcion;
	public String base64;

	@Override
	public IRequest.Validation valid() {
		var validation = new IRequest.Validation();
		if(tipoResiduoId == null)
			validation.add("Se debe especificar el tipo de residuo");
		if(descripcion == null || descripcion.isEmpty())
			validation.add("Se debe especificar la descripcion");
		if(fechaLimite != null && fechaLimite.isBefore(ZonedDateTime.now(Dates.UTC)))
			validation.add("La fecha limite de retiro no puede ser inferior a la fecha actual");
		return validation;
	}

}

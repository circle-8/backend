package org.circle8.controller.request.punto_residuo;

import org.circle8.controller.request.IRequest;

public class PostPutPuntoResiduoRequest implements IRequest {
	private final Validation validation = new Validation();
	
	public Long id;
	public Long ciudadano_id;
	public Double latitud;
	public Double longitud;
		
	@Override
	public Validation valid() {
		if ( latitud == null )
			validation.add("Se debe especificar la latitud");
		if ( longitud == null )
			validation.add("Se debe especificar la longitud");
		return validation;
	}

}

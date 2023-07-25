package org.circle8.controller.request.punto_reciclaje;

import jakarta.annotation.Nullable;
import org.circle8.controller.request.IRequest;

import java.util.List;
import java.util.Map;

public class PuntoReciclajePostRequest implements IRequest {
	private final Validation validation = new Validation();

	public List<Integer> dias;
	public List<Integer> tiposResiduo;
	public Long recicladorId;
	public Double latitud;
	public Double longitud;
	public String titulo;


	@Override
	public Validation valid() {
		if( latitud == null || longitud == null) {
			validation.add("Se debe especificar tanto latitud como longitud");
		}
		if( recicladorId == null ) {
			validation.add("Se debe especificar el id del reciclador");
		}
		if( dias.isEmpty()) {
			validation.add("Se deben especificar los días disponibles");
		}
		if( titulo == null || titulo.isEmpty()) {
			validation.add("Se debe especificar el titulo del punto");
		}
		return validation;
	}

}

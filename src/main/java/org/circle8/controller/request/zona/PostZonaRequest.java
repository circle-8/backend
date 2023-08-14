package org.circle8.controller.request.zona;

import java.util.List;

import org.circle8.controller.request.IRequest;
import org.circle8.entity.Punto;

import com.google.common.base.Strings;

public class PostZonaRequest implements IRequest{
	private final Validation validation = new Validation();
	
	public String nombre;
	public List<Punto> polyline;
	public List<Integer> tiposResiduo;

	@Override
	public Validation valid() {
		if(Strings.isNullOrEmpty(nombre))
			validation.add("Se debe especificar el nombre de la zona");
		if(polyline == null || polyline.isEmpty())
			validation.add("Se debe especificar el polyline de la zona");
		if(tiposResiduo == null || tiposResiduo.isEmpty())
			validation.add("Se debe especificar al menos un tipo de residuo");
		return validation;
	}	
}

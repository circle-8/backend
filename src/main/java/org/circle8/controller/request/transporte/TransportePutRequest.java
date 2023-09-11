package org.circle8.controller.request.transporte;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.circle8.controller.request.IRequest;

public class TransportePutRequest implements IRequest{
	private final Validation validation = new Validation();
	
	public BigDecimal precioAcordado;
	public LocalDate fechaAcordada;
	public Long transportistaId;
	
	@Override
	public Validation valid() {
		if(precioAcordado == null
				&& fechaAcordada == null
				&& transportistaId == null)
			validation.add("Se debe especificar al menos un campo.");
		
		return validation;
	}
 
}

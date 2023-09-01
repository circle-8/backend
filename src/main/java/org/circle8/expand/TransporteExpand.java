package org.circle8.expand;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class TransporteExpand {
	public static final TransporteExpand EMPTY = new TransporteExpand(false,false);
	public final boolean transportista;
	public final boolean transaccion;
	
	public TransporteExpand(List<String> expands) {
		this.transportista = expands.contains("transportista");
		this.transaccion = expands.contains("transaccion");
	}
}

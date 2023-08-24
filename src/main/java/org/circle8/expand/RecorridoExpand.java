package org.circle8.expand;

import java.util.ArrayList;
import java.util.List;

public class RecorridoExpand {
	public static final RecorridoExpand EMPTY = new RecorridoExpand(new ArrayList<>());
	public boolean zona;
	public boolean reciclador;

	public RecorridoExpand(List<String> expands) {
		this.zona = expands.contains("zona");
		this.reciclador = expands.contains("reciclador");
	}
}

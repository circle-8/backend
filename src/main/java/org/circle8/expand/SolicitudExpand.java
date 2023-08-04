package org.circle8.expand;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class SolicitudExpand {
	public static final SolicitudExpand EMPTY = new SolicitudExpand(false, false);
	public final boolean ciudadanos;
	public final boolean residuo;

	public SolicitudExpand(List<String> expands) {
		this.ciudadanos = expands.contains("ciudadanos");
		this.residuo = expands.contains("residuo");
	}
}

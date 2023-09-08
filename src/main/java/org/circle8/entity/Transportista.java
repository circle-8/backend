package org.circle8.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transportista {
	public Long id;
	public Long usuarioId;
	public List<Punto> polyline;
	
	public Transportista(long id ) { this.id = id; }

}

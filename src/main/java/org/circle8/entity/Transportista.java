package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transportista {
	public Long id;
	public Long usuarioId;
	public User user;
	public List<Punto> polyline;

	public Transportista(long id ) { this.id = id; }

}

package org.circle8.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Ciudadano {
	public long id;
	public String username;
	public String nombre;
	public final long usuarioId;

	public Ciudadano(long id, long usuarioId) {
		this.id = id;
		this.usuarioId = usuarioId;
	}
}

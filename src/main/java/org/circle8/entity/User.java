package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.dto.TipoUsuario;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	public int id;
	public String username;
	public String hashedPassword;
	public String nombre;
	public TipoUsuario tipo;
}

package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.dto.TipoUsuario;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	public long id;
	public String username;
	public String hashedPassword;
	public String nombre;
	public String email;
	public Suscripcion suscripcion;
	public TipoUsuario tipo;

	public Long ciudadanoId;

	public Long recicladorUrbanoId;
	public RecicladorUrbano reciclador;
	public Long organizacionId;
	public String razonSocial;
	public Long zonaId;

	public Long transportistaId;
}

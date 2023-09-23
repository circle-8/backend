package org.circle8.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Residuo {
	public long id;
	public long ciudadanoId;
	public ZonedDateTime fechaCreacion;
	public ZonedDateTime fechaRetiro;
	public ZonedDateTime fechaLimiteRetiro;
	public String descripcion;
	public PuntoResiduo puntoResiduo;
	public TipoResiduo tipoResiduo;
	public Transaccion transaccion;
	public Recorrido recorrido;
	public byte[] base64;
}

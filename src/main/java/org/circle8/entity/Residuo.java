package org.circle8.entity;


import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
}

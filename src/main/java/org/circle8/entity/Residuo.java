package org.circle8.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Residuo {
	public long id;
	public LocalDateTime fechaCreacion;
	public LocalDateTime fechaRetiro;	
	public LocalDateTime fechaLimiteRetiro;
	public String descripcion;
	public String puntoResiduoUri;
	public Long puntoResiduoId;
	public PuntoResiduo puntoResiduo;
	public String tipoResiduoUri;
	public Long tipoResiduoId;
	public TipoResiduo tipoResiduo;
	public String recorridoUri;
	public Long recorridoId;
	public Recorrido recorrido;
	public String transaccionUri;
	public Long transaccionId;
	public Transaccion transaccion; 
}

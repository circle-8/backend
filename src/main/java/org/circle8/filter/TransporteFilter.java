package org.circle8.filter;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransporteFilter {

	public Long id;
	public Long userId;
	public Long transportistaId;
	public Boolean entregaConfirmada;
	public Boolean pagoConfirmado;
	public Boolean soloSinTransportista;
	public LocalDate fechaRetiro;
	public Long transaccionId;
	
	public TransporteFilter(Long id) {
		this.id = id;
	}	
}

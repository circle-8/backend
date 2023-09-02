package org.circle8.controller.request.transporte;

import lombok.ToString;
import org.circle8.controller.request.IRequest;
import org.circle8.utils.Parser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@ToString
public class TransporteRequest implements IRequest {
	private final Validation validation = new Validation();

	public Long transportistaId;
	public Boolean entregaConfirmada;
	public Boolean pagoConfirmado;
	public Boolean soloSinTransportista;
	public LocalDate fechaRetiro;
	public Long transaccionId;
	
	public TransporteRequest(Map<String, List<String>> queryParams) {
		this.transportistaId = Parser.parseLong(validation, queryParams, "transportista_id");
		this.entregaConfirmada = Parser.parseBoolean(validation, queryParams, "entrega_confirmada");
		this.pagoConfirmado = Parser.parseBoolean(validation, queryParams, "pago_confirmado");
		this.soloSinTransportista = Parser.parseBoolean(validation, queryParams, "solo_sin_transportista");
		this.fechaRetiro = Parser.parseLocalDate(validation, queryParams, "fecha_retiro");
		this.transaccionId = Parser.parseLong(validation, queryParams, "transaccion_id");
	}

	@Override
	public Validation valid() {
		return validation;
	}
}

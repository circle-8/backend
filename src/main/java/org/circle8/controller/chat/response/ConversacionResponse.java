package org.circle8.controller.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.response.ApiResponse;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ConversacionResponse implements ApiResponse {
	public enum Type {
		TRANSACCION_TRANSPORTES, TRANSACCION_RECIBOS, TRANSACCION_ENTREGAS,
		RECORRIDO_TRANSPORTES, RECORRIDO_ENTREGAS;
	}
	public String id;
	public String titulo;
	public String descripcion;
	public Type type;
	public Long externalId;
	public Long residuoId;
	public String chatsUri;
	public boolean newMessages;
	public ZonedDateTime timestamp;
}

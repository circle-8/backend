package org.circle8.controller.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.response.ApiResponse;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ConversacionResponse implements ApiResponse {
	public enum Type {
		RECORRIDO, TRANSACCION;
	}
	public String id;
	public String titulo;
	public String descripcion;
	public Type type;
	public Long externalId;
	public String chatsUri;
}

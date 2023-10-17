package org.circle8.controller.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.response.ApiResponse;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ChatResponse implements ApiResponse {
	public enum Type {
		CIUDADANO, RECICLADOR, RECICLADOR_URBANO, TRANSPORTISTA
	}
	public String id;
	public String titulo;
	public String descripcion;
	public Type type;
	public Long externalId;
	public String chatHistoryUri;
	public String actionsUri;
	public String chatWs;
	public boolean newMessages;
	public ZonedDateTime timestamp;
}

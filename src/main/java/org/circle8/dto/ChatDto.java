package org.circle8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.chat.response.ChatResponse;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDto {
	/**
	 * conv_id+u1+u2
	 * Para transaccion:
	 * RECICLADOR+CIUDADANO
	 * RECICLADOR+TRANSPORTISTA
	 * TRANSPORTISTA+CIUDADANO
	 * Para recorrido:
	 * RECICLADOR+CIUDADANO
	 */
	public String id;
	public String titulo;
	public String descripcion;
	public ChatResponse.Type type;
	public Long externalId;
	public boolean newMessages;
	public ZonedDateTime timestamp;
	public UserDto user;

	public ChatResponse toResponse() {
		return new ChatResponse(
			this.id,
			this.titulo,
			this.descripcion,
			this.type,
			this.externalId,
			String.format("/chat/%s/history?user_id=%s", this.id, this.user.id),
			String.format("/chat/%s/actions?user_id=%s", this.id, this.user.id),
			String.format("/chat/%s?user_id=%s", this.id, this.user.id),
			this.newMessages,
			this.timestamp
		);
	}
}

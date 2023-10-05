package org.circle8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.chat.response.ConversacionResponse;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversacionDto {
	public String id;
	public String titulo;
	public String descripcion;
	public ConversacionResponse.Type type;
	public Long externalId;
	public Long residuoId;
	public boolean newMessages;
	public ZonedDateTime timestamp;
	public ZonedDateTime fechaConversacion; // Fecha en la que se efectua o se creó
	public UserDto user;

	public ConversacionResponse toResponse() {
		return new ConversacionResponse(
			this.id,
			this.titulo,
			this.descripcion,
			this.type,
			this.externalId,
			this.residuoId,
			String.format("/user/%s/conversacion/%s/chats", this.user.id, this.id),
			this.newMessages,
			this.timestamp
		);
	}
}

package org.circle8.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.entity.Mensaje;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MensajeDto {
	private static final Gson GSON = new Gson();

	public ChatMessageResponse.Type type;
	public ZonedDateTime timestamp;
	public Long from;
	public Long to;
	public ChatMessageResponse.Message message;
	public List<ChatMessageResponse.Action> availableActions;

	public static MensajeDto from(Mensaje entity, List<ChatMessageResponse.Action> actions) {
		if (entity == null) return null;
		return new MensajeDto(
			entity.type,
			entity.timestamp,
			entity.from,
			entity.to,
			GSON.fromJson(entity.message, ChatMessageResponse.MessageResponse.class), // TODO components
			actions
		);
	}

	public ChatMessageResponse toResponse() {
		return new ChatMessageResponse(
			this.type,
			this.timestamp,
			this.from,
			this.to,
			this.message,
			this.availableActions
		);
	}
}

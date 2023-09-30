package org.circle8.service;

import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ActionService {
	enum ActionType {
		MESSAGE,
		COMIENZO_PROPONER_PRECIO,
		ATRAS_MODAL_PROPONER_PRECIO,
		OK_MODAL_PROPONER_PRECIO,
		OTRO_PROPONER_PRECIO,
		ACEPTAR_PROPONER_PRECIO,
	}

	public List<ChatMessageResponse.Action> availableActions(Long fromUser, Long toUser) {
		return List.of();
	}

	public Map<Long, ChatMessageResponse> execute(MessageRequest req, Long fromUser, Long toUser) {
		// TODO validaciones
		var type = ActionType.valueOf(req.type);

		return switch ( type ) {
			case MESSAGE -> {
				var message = new ChatMessageResponse(
					ChatMessageResponse.Type.MESSAGE,
					ZonedDateTime.now(Dates.UTC),
					fromUser,
					toUser,
					new ChatMessageResponse.MessageResponse(
						req.message,
						"primary"
					),
					availableActions(fromUser, toUser)
				);
				yield Map.of(
					fromUser, message,
					toUser, message
				);
			}
			default -> Map.of();
		};
	}
}

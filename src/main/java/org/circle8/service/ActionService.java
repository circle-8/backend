package org.circle8.service;

import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.utils.Dates;

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


		// TODO DB

		return switch ( type ) {
			case MESSAGE -> {
				var message = new ChatMessageResponse(
					ChatMessageResponse.Type.MESSAGE,
					Dates.now(),
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
			case COMIENZO_PROPONER_PRECIO -> {
				// TODO: esto es un mock
				yield Map.of(
					fromUser, new ChatMessageResponse(
						ChatMessageResponse.Type.COMPONENT,
						Dates.now(),
						null,
						fromUser,
						new ChatMessageResponse.ComponentResponse(
							ChatMessageResponse.ComponentMessageType.MODAL,
							List.of(
								new ChatMessageResponse.Component(
									ChatMessageResponse.ComponentType.TITLE,
									"Acordar tarifa",
									null, null, null
								),
								new ChatMessageResponse.Component(
									ChatMessageResponse.ComponentType.TEXT,
									"Por favor seleccione un importe:",
									null, null, null
								),
								new ChatMessageResponse.Component(
									ChatMessageResponse.ComponentType.INPUT,
									null,
									"importe",
									ChatMessageResponse.InputType.NUMBER,
									null
								),
								new ChatMessageResponse.Component(
									ChatMessageResponse.ComponentType.BUTTON,
									"Cancelar",
									null,
									null,
									new ChatMessageResponse.Action(
										ChatMessageResponse.ActionType.ACTION,
										"",
										MessageRequest.builder().type(ActionType.ATRAS_MODAL_PROPONER_PRECIO.name()).build()
									)
								),
								new ChatMessageResponse.Component(
									ChatMessageResponse.ComponentType.BUTTON,
									"Proponer",
									null,
									null,
									new ChatMessageResponse.Action(
										ChatMessageResponse.ActionType.ACTION,
										"",
										MessageRequest.builder().type(ActionType.OK_MODAL_PROPONER_PRECIO.name()).build()
									)
								)
							)
						),
						availableActions(fromUser, toUser)
					)
				);
			}
			default -> Map.of();
		};
	}
}

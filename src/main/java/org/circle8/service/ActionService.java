package org.circle8.service;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.circle8.controller.chat.ChatController;
import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.dao.MensajeDao;
import org.circle8.dto.MensajeDto;
import org.circle8.entity.Mensaje;
import org.circle8.exception.PersistenceException;
import org.circle8.utils.Dates;

import java.util.List;
import java.util.Map;

public class ActionService {
	private static final Gson GSON = new Gson();
	private final MensajeDao mensajes;

	@Inject
	public ActionService(MensajeDao mensajes) {
		this.mensajes = mensajes;
	}

	enum ActionType {
		MESSAGE,
		COMIENZO_PROPONER_PRECIO,
		ATRAS_MODAL_PROPONER_PRECIO,
		OK_MODAL_PROPONER_PRECIO,
		OTRO_PROPONER_PRECIO,
		ACEPTAR_PROPONER_PRECIO,
	}

	public List<ChatMessageResponse.Action> availableActions(ChatController.SavedSession session) {
		return List.of();
	}

	public Map<Long, ChatMessageResponse> execute(MessageRequest req, ChatController.SavedSession session) {
		// TODO validaciones
		var type = ActionType.valueOf(req.type);


		// TODO DB

		return switch ( type ) {
			case MESSAGE -> {
				var component = new ChatMessageResponse.MessageResponse(req.message, "primary");
				var msg = new Mensaje(
					null,
					ChatMessageResponse.Type.MESSAGE,
					Dates.now(),
					session.fromUser(),
					session.toUser(),
					GSON.toJson(component),
					false,
					session.type() == ChatService.ConversacionType.RECORRIDO ? session.idConv() : null,
					session.type() == ChatService.ConversacionType.TRANSACCION ? session.idConv() : null
				);
				try {
					mensajes.save(msg);
				} catch ( PersistenceException e ) {
					// TODO error handling
				}

				var res = MensajeDto.from(msg).toResponse();
				res.availableActions = availableActions(session);

				yield Map.of(
					session.fromUser(), res,
					session.toUser(), res
				);
			}
			case COMIENZO_PROPONER_PRECIO -> {
				// TODO: esto es un mock
				yield Map.of(
					session.fromUser(), new ChatMessageResponse(
						ChatMessageResponse.Type.COMPONENT,
						Dates.now(),
						null,
						session.fromUser(),
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
						availableActions(session)
					)
				);
			}
			default -> Map.of();
		};
	}
}

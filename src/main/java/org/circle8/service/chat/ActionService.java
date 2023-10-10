package org.circle8.service.chat;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.circle8.controller.chat.ChatController;
import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.dao.MensajeDao;
import org.circle8.dto.MensajeDto;
import org.circle8.entity.Mensaje;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.update.UpdateMensaje;

import java.util.List;
import java.util.Map;

public class ActionService {
	private static final Gson GSON = new Gson();
	private final MensajeDao mensajes;
	private final ComponentUtils utils;

	@Inject
	public ActionService(
		MensajeDao mensajes,
		ComponentUtils utils
	) {
		this.mensajes = mensajes;
		this.utils = utils;
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
		// TODO: mocked
		return List.of(
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.MESSAGE, "", null),
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.ACTION, "Acordar precio", new MessageRequest("COMIENZO_PROPONER_PRECIO", "", null, null))
		);
	}

	public Map<Long, ChatMessageResponse> execute(MessageRequest req, ChatController.SavedSession session) throws ServiceException {
		// TODO validaciones
		var type = ActionType.valueOf(req.type);

		record Tuple(List<Mensaje> messagesToSave, Map<Long, ChatMessageResponse> responses) {}

		try ( var t = mensajes.open() ) {
			var response = switch ( type ) {
				case MESSAGE -> {
					var msg = utils.buildMessage(
						session.fromUser(),
						session.toUser(),
						utils.message(req.message, "primary"),
						session
					);

					var res = MensajeDto.from(msg).toResponse();
					res.availableActions = availableActions(session);

					yield new Tuple(List.of(msg), Map.of(
						session.fromUser(), res,
						session.toUser(), res
					));
				}
				case COMIENZO_PROPONER_PRECIO -> {
					var component = utils.modalProponerPrecio();
					var msg = utils.buildMessage(
						null,
						session.fromUser(),
						component,
						session
					);

					var res = MensajeDto.from(msg).toResponse();
					yield new Tuple(List.of(msg), Map.of(
						session.fromUser(), res
					));
				}
				case ATRAS_MODAL_PROPONER_PRECIO -> {
					var toAck = Long.parseLong((String) req.extraData.get("ack"));
					mensajes.update(UpdateMensaje.builder().id(toAck).ack(true).build());
					yield new Tuple(List.of(), Map.of());
				}
				case OK_MODAL_PROPONER_PRECIO -> {
					var toAck = Long.parseLong((String) req.extraData.get("ack"));
					mensajes.update(UpdateMensaje.builder().id(toAck).ack(true).build());

					// TODO

					yield new Tuple(List.of(), Map.of());
				}
				default -> new Tuple(List.of(), Map.of());
			};

			for ( var m : response.messagesToSave )
				mensajes.save(t, m);
			t.commit();

			return response.responses;
		} catch ( PersistenceException e ) {
			throw new ServiceError("error saving messages", e);
		}
	}
}

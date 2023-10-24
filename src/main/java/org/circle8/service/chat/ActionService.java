package org.circle8.service.chat;

import com.google.gson.Gson;
import com.google.inject.Inject;
import lombok.val;
import org.circle8.controller.chat.ChatController;
import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.dao.MensajeDao;
import org.circle8.dao.TransaccionDao;
import org.circle8.dao.TransporteDao;
import org.circle8.dao.UserDao;
import org.circle8.dto.MensajeDto;
import org.circle8.entity.Mensaje;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransaccionExpand;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.TransporteFilter;
import org.circle8.update.UpdateMensaje;
import org.circle8.update.UpdateTransporte;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionService {
	private static final Gson GSON = new Gson();
	private final MensajeDao mensajes;
	private final UserDao users;
	private final TransporteDao transportes;
	private final TransaccionDao transacciones;
	private final ComponentUtils utils;

	@Inject
	public ActionService(
		MensajeDao mensajes,
		UserDao users,
		TransporteDao transportes,
		TransaccionDao transacciones,
		ComponentUtils utils
	) {
		this.mensajes = mensajes;
		this.users = users;
		this.transportes = transportes;
		this.transacciones = transacciones;
		this.utils = utils;
	}

	public enum ActionType {
		MESSAGE,
		COMIENZO_PROPONER_PRECIO,
		ATRAS_MODAL_PROPONER_PRECIO,
		OK_MODAL_PROPONER_PRECIO,
		OTRO_PROPONER_PRECIO,
		ACEPTAR_PROPONER_PRECIO,
	}

	public List<ChatMessageResponse.Action> availableActions(
		ActionType type,
		ChatService.ConversacionType convType,
		Long idConv,
		Long u1,
		Long u2,
		Long from
	) {
		if ( type == ActionType.MESSAGE && convType == ChatService.ConversacionType.TRANSACCION ) {
			try {
				val x = TransaccionExpand.builder().transporte(true).build();
				val tr = transacciones.get(idConv, x).orElseThrow(() -> new PersistenceException("transaccion inexistente"));
				val user1 = users.get(null, u1).orElseThrow(() -> new PersistenceException(""));
				val user2 = users.get(null, u2).orElseThrow(() -> new PersistenceException(""));
				val possibleTr = Arrays.asList(user1.transportistaId, user2.transportistaId);
				val possibleRec = Arrays.asList(user1.ciudadanoId, user2.ciudadanoId);
				if (
					   tr.transporte != null
					&& possibleTr.contains(tr.transporte.transportistaId)
					&& possibleRec.contains(tr.puntoReciclaje.recicladorId)
				) {
					return List.of(
						new ChatMessageResponse.Action(ChatMessageResponse.ActionType.MESSAGE, "", null),
						new ChatMessageResponse.Action(
							ChatMessageResponse.ActionType.ACTION,
							"Acordar precio",
							utils.request(ActionType.COMIENZO_PROPONER_PRECIO)
						)
					);
				}
			} catch ( PersistenceException e ) {
				// TODO
			}
		}
		return List.of(
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.MESSAGE, "", null)
		);
	}

	public List<ChatMessageResponse.Action> availableActions(
		ActionType type,
		ChatController.SavedSession session,
		Long userId
	) {
		return availableActions(type, session.type(), session.idConv(), session.fromUser(), session.toUser(), session.fromUser());
	}

	public Map<Long, ChatMessageResponse> execute(MessageRequest req, ChatController.SavedSession session) throws ServiceException {
		// TODO validaciones
		var type = ActionType.valueOf(req.type);

		record Tuple(List<Mensaje> messagesToSave, Map<Long, Mensaje> responses) {}

		try ( var t = mensajes.open() ) {
			if ( req.extraData != null && req.extraData.get(ExtraData.ACK) != null ) {
				var toAck = Long.parseLong((String) req.extraData.get(ExtraData.ACK));
				mensajes.update(UpdateMensaje.builder().id(toAck).ack(true).build());
			}

			var response = switch ( type ) {
				case MESSAGE -> {
					var msg = utils.buildMessage(
						session.fromUser(),
						session.toUser(),
						utils.message(req.message, "primary"),
						session
					);

					yield new Tuple(List.of(msg), Map.of(
						session.fromUser(), msg,
						session.toUser(), msg
					));
				}
				case COMIENZO_PROPONER_PRECIO, OTRO_PROPONER_PRECIO -> {
					var component = utils.modalProponerPrecio();
					var msg = utils.buildMessage(
						null,
						session.fromUser(),
						component,
						session
					);

					yield new Tuple(List.of(msg), Map.of(
						session.fromUser(), msg
					));
				}
				case OK_MODAL_PROPONER_PRECIO -> {
					var importe = new BigDecimal(req.inputs.get(Inputs.IMPORTE)).toBigInteger();
					var component = utils.message("Nuevo importe propuesto: $" + importe, "info");
					var serverMessage = utils.buildMessage(
						null,
						session.fromUser(),
						component,
						session
					);

					var modal = utils.modalPrecioPropuesto(importe);
					var propuestoMsg = utils.buildMessage(
						null,
						session.toUser(),
						modal,
						session
					);

					yield new Tuple(List.of(serverMessage, propuestoMsg), Map.of(
						session.fromUser(), serverMessage,
						session.toUser(), propuestoMsg
					));
				}
				case ACEPTAR_PROPONER_PRECIO -> {
					var importe = BigDecimal.valueOf((Double) req.extraData.get(ExtraData.IMPORTE));

					var idTransporte = transportes.get(
						t,
						TransporteFilter.builder().transaccionId(session.idConv()).build(),
						TransporteExpand.EMPTY
					).map(tr -> tr.id).orElse(0L);
					var update = UpdateTransporte.builder()
						.id(idTransporte)
						.precioAcordado(importe)
						.build();
					transportes.update(t, update);

					var component = utils.message("Importe aceptado: $" + importe.toBigInteger(), "info");
					var toRequester = utils.buildMessage(
						null,
						session.fromUser(),
						component,
						session
					);
					var toResponder = utils.buildMessage(
						null,
						session.toUser(),
						component,
						session
					);

					yield new Tuple(List.of(toRequester, toResponder), Map.of(
						session.fromUser(), toRequester,
						session.toUser(), toResponder
					));
				}
				default -> new Tuple(List.of(), Map.of());
			};

			for ( var m : response.messagesToSave )
				mensajes.save(t, m);
			t.commit();

			record Entry(Long user, MensajeDto mensaje) {}
			return response.responses.entrySet().stream()
				.map(e -> new Entry(
					e.getKey(),
					MensajeDto.from(e.getValue(), availableActions(type, session, e.getKey()))
				))
				.collect(Collectors.toMap(e -> e.user, e -> e.mensaje.toResponse()));
		} catch ( PersistenceException e ) {
			throw new ServiceError("error saving messages", e);
		}
	}
}

package org.circle8.service.chat;

import com.google.gson.Gson;
import com.google.inject.Singleton;
import org.circle8.controller.chat.ChatController;
import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.entity.Mensaje;
import org.circle8.utils.Dates;

import java.util.List;
import java.util.Map;

@Singleton
public class ComponentUtils {
	private static final Gson GSON = new Gson();

	public ChatMessageResponse.MessageResponse message(String message, String color) {
		return new ChatMessageResponse.MessageResponse(message, color);
	}

	public ChatMessageResponse.Component title(String title) {
		return new ChatMessageResponse.Component(ChatMessageResponse.ComponentType.TITLE, title, null, null, null);
	}

	public ChatMessageResponse.Component text(String text) {
		return new ChatMessageResponse.Component(ChatMessageResponse.ComponentType.TEXT, text, null, null, null);
	}

	public ChatMessageResponse.Component input(Inputs nombre, ChatMessageResponse.InputType type) {
		return new ChatMessageResponse.Component(ChatMessageResponse.ComponentType.INPUT, null, nombre.name(), type, null);
	}

	public ChatMessageResponse.Component button(String nombre, ChatMessageResponse.Action action) {
		return new ChatMessageResponse.Component(ChatMessageResponse.ComponentType.BUTTON, nombre, null, null, action);
	}

	public ChatMessageResponse.Action action(MessageRequest req) {
		return new ChatMessageResponse.Action(ChatMessageResponse.ActionType.ACTION, "", req);
	}

	public MessageRequest request(ActionService.ActionType action) {
		return MessageRequest.builder()
			.type(action.name())
			.build();
	}

	public MessageRequest ackRequest(ActionService.ActionType action) {
		return MessageRequest.builder()
				.type(action.name())
				.extraData(Map.of(ExtraData.ACK, "{id}"))
				.build();
	}

	public Mensaje buildMessage(Long from, Long to, ChatMessageResponse.Message msg, ChatController.SavedSession session) {
		return new Mensaje(
			null,
			msg instanceof ChatMessageResponse.MessageResponse ? ChatMessageResponse.Type.MESSAGE : ChatMessageResponse.Type.COMPONENT,
			Dates.now(),
			from,
			to,
			GSON.toJson(msg),
			false,
			session.type() == ChatService.ConversacionType.RECORRIDO ? session.idConv() : null,
			session.type() == ChatService.ConversacionType.TRANSACCION ? session.idConv() : null,
			session.chatId()
		);
	}

	public ChatMessageResponse.ComponentResponse modalProponerPrecio() {
		return new ChatMessageResponse.ComponentResponse(
			ChatMessageResponse.ComponentMessageType.MODAL,
			List.of(
				title("Acordar precio"),
				text("Por favor seleccione un importe:"),
				input(Inputs.IMPORTE, ChatMessageResponse.InputType.NUMBER),
				button(
					"Cancelar",
					action(ackRequest(ActionService.ActionType.ATRAS_MODAL_PROPONER_PRECIO))
				),
				button(
					"Proponer",
					action(ackRequest(ActionService.ActionType.OK_MODAL_PROPONER_PRECIO))
				)
			)
		);
	}

	public ChatMessageResponse.ComponentResponse modalPrecioPropuesto(Number importe) {
		return new ChatMessageResponse.ComponentResponse(
			ChatMessageResponse.ComponentMessageType.MODAL,
			List.of(
				title("Nuevo precio propuesto"),
				text("El nuevo precio propuesto para el transporte es de $ " + importe),
				text("Podés elegir otro o aceptarlo"),
				button(
					"Otro",
					action(ackRequest(ActionService.ActionType.OTRO_PROPONER_PRECIO))
				),
				button(
					"Aceptar",
					action(MessageRequest.builder()
						.type(ActionService.ActionType.ACEPTAR_PROPONER_PRECIO.name())
						.extraData(Map.of(ExtraData.ACK, "{id}", ExtraData.IMPORTE, importe))
						.build())
				)
			)
		);
	}
}

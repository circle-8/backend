package org.circle8.controller.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.chat.request.MessageRequest;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.dto.ChatDto;
import org.circle8.dto.ConversacionDto;
import org.circle8.dto.MensajeDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.service.ActionService;
import org.circle8.service.ChatService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Singleton
@Slf4j
public class ChatController implements Consumer<WsConfig> {
	public record SavedSession(ChatService.ConversacionType type, Long idConv, Long fromUser, Long toUser) {}
	private static final Map<WsContext, SavedSession> connUsers = new ConcurrentHashMap<>();

	private final ActionService actions;
	private final ChatService service;

	@Inject
	public ChatController(ActionService actions, ChatService service) {
		this.actions = actions;
		this.service = service;
	}

	/**
	 * GET /user/{user_id}/conversaciones
	 */
	public ApiResponse conversaciones(Context ctx) {
		// TODO: validaciones
		var user = ctx.pathParamAsClass("user_id", Long.class).get();
		try {
			val convs = service.list(user).stream()
				.map(ConversacionDto::toResponse)
				.toList();
			return new ListResponse<>(convs);
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error list conversaciones", user, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * GET /user/{user_id}/conversacion/{conversacion_id}/chats
	 */
	public ApiResponse chats(Context ctx) {
		// TODO validaciones
		val id = ctx.pathParam("conversacion_id");
		val user = ctx.pathParamAsClass("user_id", Long.class).get();
		try {
			val split = id.split("-");
			val type = split[0].equals("TRA")
				? ChatService.ConversacionType.TRANSACCION
				: ChatService.ConversacionType.RECORRIDO;
			val idConv = Long.parseLong(split[1]);

			val chats = service.chats(type, idConv, user).stream()
				.map(ChatDto::toResponse)
				.toList();
			return new ListResponse<>(chats);
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error list chats", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * GET /chat/{chat_id}/history
	 */
	public ApiResponse history(Context ctx) {
		// TODO: validaciones
		var id = ctx.pathParam("chat_id");
		var split = id.split("\\+");

		val conv = split[0];
		val convSplit = conv.split("-");
		val type = convSplit[0].equals("TRA")
			? ChatService.ConversacionType.TRANSACCION
			: ChatService.ConversacionType.RECORRIDO;
		val idConv = Long.parseLong(convSplit[1]);

		val u1 = Long.parseLong(split[1]);
		val u2 = Long.parseLong(split[2]);

		try {
			val mensajes = service.mensajes(type, idConv, u1, u2).stream()
				.map(MensajeDto::toResponse)
				.toList();
			return new ListResponse<>(mensajes);
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error list mensajes", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}

		/* LO DEJO COMO EJEMPLO PARA ACCIONES
		return new ListResponse<>(List.of(
			new ChatMessageResponse(
				ChatMessageResponse.Type.COMPONENT,
				ZonedDateTime.now().minusMinutes(15),
				2L,
				1L,
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
								MessageRequest.builder().type("CANCELAR ACORDAR").build()
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
								MessageRequest.builder().type("ACORDAR").build()
							)
						)
					)
				),
				null
			),
			new ChatMessageResponse(
				ChatMessageResponse.Type.MESSAGE,
				ZonedDateTime.now().minusMinutes(15),
				2L,
				1L,
				new ChatMessageResponse.MessageResponse("Hola que tal", "primary"),
				null
			)
		));
		*/
	}

	/**
	 * GET /chat/{chat_id}/actions
	 */
	public ApiResponse actions(Context ctx) {
		return new ListResponse<>(List.of(
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.MESSAGE, "", null),
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.ACTION, "Acordar precio", new MessageRequest())
		));
	}

	/**
	 * WebSocket para el chat de usuarios.
	 * /chat/{chat_id}?user_id=
	 *  chat_id = conversacion_id+user_id+user_id2
	 */
	@Override
	public void accept(WsConfig ws) {
		ws.onConnect(ctx -> {
			var chatId = ctx.pathParam("chat_id");

			// TODO: validaciones
			var split = chatId.split("\\+");

			val conv = split[0];
			val convSplit = conv.split("-");
			val type = convSplit[0].equals("TRA")
				? ChatService.ConversacionType.TRANSACCION
				: ChatService.ConversacionType.RECORRIDO;
			val idConv = Long.parseLong(convSplit[1]);

			var user1 = split[1];
			var user2 = split[2];

			// TODO: validaciones
			var fromUser = ctx.queryParam("user_id");
			assert fromUser != null;
			var toUser = fromUser.equals(user1) ? user2 : user1;

			ctx.enableAutomaticPings(15, TimeUnit.SECONDS);

			connUsers.put(ctx, new SavedSession(type, idConv, Long.parseLong(fromUser), Long.parseLong(toUser)));
		});
		ws.onClose(ctx -> {
			ctx.disableAutomaticPings();
			connUsers.remove(ctx);
		});
		ws.onMessage(ctx -> {
			var mess = ctx.messageAsClass(MessageRequest.class);
			var sessions = connUsers.get(ctx);

			var responses = actions.execute(mess, sessions);

			responses.forEach((to, res) -> connUsers.entrySet()
				.stream()
				.filter(e -> e.getValue().fromUser.equals(to))
				.findFirst()
				.map(Map.Entry::getKey)
				.ifPresent(c -> c.sendAsClass(res, ChatMessageResponse.class)));
		});
	}
}

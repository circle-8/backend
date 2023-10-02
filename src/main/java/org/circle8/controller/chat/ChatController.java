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
import org.circle8.controller.chat.response.ChatResponse;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.dto.ConversacionDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.service.ActionService;
import org.circle8.service.ChatService;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Singleton
@Slf4j
public class ChatController implements Consumer<WsConfig> {
	record SavedSession(Long fromUser, Long toUser) {}
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

		// var mock = new ConversacionResponse(
		// 	"TRA-1",
		// 	"Titulo 1 de prueba",
		// 	"Descripcion de prueba\ncon salto de linea",
		// 	ConversacionResponse.Type.TRANSACCION,
		// 	1L,
		// 	String.format("/user/%s/conversacion/TRA-1/chats", user),
		// 	true,
		// 	Dates.now().minusMinutes(10)
		// );
		// var mock2 = mock.toBuilder()
		// 	.id("REC-1")
		// 	.type(ConversacionResponse.Type.RECORRIDO)
		// 	.chatsUri(String.format("/user/%s/conversacion/REC-1/chats", user))
		// 	.build();

		// return new ListResponse<>(List.of(
		// 	mock,
		// 	mock2
		// ));
	}

	/**
	 * GET /user/{user_id}/conversacion/{conversacion_id}/chats
	 */
	public ApiResponse chats(Context ctx) {
		var id = ctx.pathParam("conversacion_id");
		var user = ctx.pathParam("user_id");
		if ( "TRA-1".equals(id) ) {
			// Punto de vista del transportador
			return new ListResponse<>(List.of(
				new ChatResponse(
					String.format("%s-%s-1", id, user),
					"Entrega glopez",
					"Residuo XYZ",
					ChatResponse.Type.CIUDADANO,
					1L,
					String.format("/chat/%s+%s+1/history", id, user),
					String.format("/chat/%s+%s+1/actions?user_id=%s", id, user, user),
					String.format("/chat/%s+%s+1?user_id=%s", id, user, user),
					true,
					Dates.now().minusMinutes(10)
				),
				new ChatResponse(
					String.format("%s+%s+2", id, user),
					"Recibe mgiordano",
					"",
					ChatResponse.Type.RECICLADOR,
					2L,
					String.format("/chat/%s+2+%s/history", id, user),
					String.format("/chat/%s+2+%s/actions?user_id=%s", id, user, user),
					String.format("/chat/%s+2+%s?user_id=%s", id, user, user),
					false,
					Dates.now().minusMinutes(15)
				)
			));
		} else {
			// Punto de vista del reciclador urbano
			return new ListResponse<>(List.of(
				new ChatResponse(
					String.format("%s+%s+1", id, user),
					"Entrega hkozak",
					"Residuo XYZ",
					ChatResponse.Type.CIUDADANO,
					1L,
					String.format("/chat/%s+%s+1/history", id, user),
					String.format("/chat/%s+%s+1/actions?user_id=%s", id, user, user),
					String.format("/chat/%s+%s+1?user_id=%s", id, user, user),
					true,
					Dates.now().minusMinutes(1)
				),
				new ChatResponse(
					String.format("%s+%s+2", id, user),
					"Entrega hvaldez",
					"",
					ChatResponse.Type.CIUDADANO,
					2L,
					String.format("/chat/%s+%s+2/history", id, user),
					String.format("/chat/%s+%s+2/actions?user_id=%s", id, user, user),
					String.format("/chat/%s+%s+2?user_id=%s", id, user, user),
					false,
					Dates.now().minusMinutes(60)
				)
			));
		}
	}

	/**
	 * GET /chat/{chat_id}/history
	 */
	public ApiResponse history(Context ctx) {
		var id = ctx.pathParam("chat_id");
		if ( Pattern.matches("(TRA|REC)-1\\+\\d+\\+1", id) ) {
			return new ListResponse<>(List.of(
				new ChatMessageResponse(
					ChatMessageResponse.Type.MESSAGE,
					ZonedDateTime.now().minusMinutes(15),
					2L,
					1L,
					new ChatMessageResponse.MessageResponse("Hola que tal", "primary"),
					null
				),
				new ChatMessageResponse(
					ChatMessageResponse.Type.MESSAGE,
					ZonedDateTime.now().minusMinutes(5),
					1L,
					2L,
					new ChatMessageResponse.MessageResponse("Como te va\nTodo bien????", "primary"),
					null
				)
			));
		} else {
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
		}
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
			var chatSplit = chatId.split("\\+");
			var conversacionId = chatSplit[0];
			var user1 = chatSplit[1];
			var user2 = chatSplit[2];

			// TODO: validaciones
			var fromUser = ctx.queryParam("user_id");
			assert fromUser != null;
			var toUser = fromUser.equals(user1) ? user2 : user1;

			ctx.enableAutomaticPings(15, TimeUnit.SECONDS);

			connUsers.put(ctx, new SavedSession(Long.parseLong(fromUser), Long.parseLong(toUser)));
		});
		ws.onClose(ctx -> {
			ctx.disableAutomaticPings();
			connUsers.remove(ctx);
		});
		ws.onMessage(ctx -> {
			var mess = ctx.messageAsClass(MessageRequest.class);
			var sessions = connUsers.get(ctx);

			var responses = actions.execute(mess, sessions.fromUser, sessions.toUser);

			responses.forEach((to, res) -> connUsers.entrySet()
				.stream()
				.filter(e -> e.getValue().fromUser.equals(to))
				.findFirst()
				.map(Map.Entry::getKey)
				.ifPresent(c -> c.sendAsClass(res, ChatMessageResponse.class)));
		});
	}
}

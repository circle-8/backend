package org.circle8.controller.chat;

import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import org.circle8.controller.chat.response.ChatMessageResponse;
import org.circle8.controller.chat.response.ChatResponse;
import org.circle8.controller.chat.response.ConversacionResponse;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ListResponse;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Singleton
@Slf4j
public class ChatController {
	/**
	 * GET /user/{user_id}/conversaciones
	 */
	public ApiResponse conversaciones(Context ctx) {
		var user = ctx.pathParam("user_id");
		var mock = new ConversacionResponse(
			"TRA-1",
			"Titulo 1 de prueba",
			"Descripcion de prueba\ncon salto de linea",
			ConversacionResponse.Type.TRANSACCION,
			1L,
			String.format("/user/%s/conversacion/TRA-1/chats", user)
		);
		var mock2 = mock.toBuilder()
			.id("REC-1")
			.type(ConversacionResponse.Type.RECORRIDO)
			.chatsUri(String.format("/user/%s/conversacion/REC-1/chats", user))
			.build();

		return new ListResponse<>(List.of(
			mock,
			mock2
		));
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
					String.format("/chat/%s-%s-1/history", id, user),
					String.format("/chat/%s-%s-1/actions?user_id=%s", id, user, user),
					"ws://not-implemented"
				),
				new ChatResponse(
					String.format("%s-%s-2", id, user),
					"Recibe mgiordano",
					"",
					ChatResponse.Type.RECICLADOR,
					2L,
					String.format("/chat/%s-2-%s/history", id, user),
					String.format("/chat/%s-2-%s/actions?user_id=%s", id, user, user),
					"ws://not-implemented"
				)
			));
		} else {
			// Punto de vista del reciclador urbano
			return new ListResponse<>(List.of(
				new ChatResponse(
					String.format("%s-%s-1", id, user),
					"Entrega hkozak",
					"Residuo XYZ",
					ChatResponse.Type.CIUDADANO,
					1L,
					String.format("/chat/%s-%s-1/history", id, user),
					String.format("/chat/%s-%s-1/actions?user_id=%s", id, user, user),
					"ws://not-implemented"
				),
				new ChatResponse(
					String.format("%s-%s-2", id, user),
					"Entrega hvaldez",
					"",
					ChatResponse.Type.CIUDADANO,
					2L,
					String.format("/chat/%s-%s-2/history", id, user),
					String.format("/chat/%s-%s-2/actions?user_id=%s", id, user, user),
					"ws://not-implemented"
				)
			));
		}
	}

	/**
	 * GET /chat/{chat_id}/history
	 */
	public ApiResponse history(Context ctx) {
		var id = ctx.pathParam("chat_id");
		if ( Pattern.matches("(TRA|REC)-1-\\d+-1", id) ) {
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
									"{\"type\": \"CANCELAR_ACORDAR\"}"
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
									"{\"type\": \"ACORDAR\"}"
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
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.MESSAGE, "", ""),
			new ChatMessageResponse.Action(ChatMessageResponse.ActionType.ACTION, "Acordar precio", "{}")
		));
	}

	// TODO: WebSocket
}

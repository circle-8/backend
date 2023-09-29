package org.circle8.controller.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.response.ApiResponse;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ChatMessageResponse implements ApiResponse {
	/**
	 * Los tipos de mensaje para la respuesta.
	 * MESSAGE es el mensaje que se le muestra al usuario como cualquier mensaje en un chat.
	 * Puede venir de otro usuario o del server.
	 * COMPONENT representa un componente del tipo {@link ComponentMessageType}
	 * ERROR es igual que un mensaje, pero siempre proveniente del server y no se le muestra al user
	 */
	public enum Type {
		MESSAGE, COMPONENT, ERROR
	}

	/**
	 * Los tipos de componentes que puede renderizar el frontend:
	 * MESSAGE es un mensaje que se muestra en el chat del usuario, proveniente del sistema, pero a
	 * diferencia del tipo MESSAGE de {@link Type}, es un mensaje que puede tener asociada una
	 * accion.
	 * MODAL es un componente tipo pop up que se muestra sobre el chat del usuario. Suele tener
	 * asociada una accion.
	 */
	public enum ComponentMessageType {
		MESSAGE, MODAL
	}

	/**
	 * Los tipos de componentes que van a incluirse en el mensaje de componentes
	 * TITLE representa el titulo que va en el header de un modal
	 * TEXT representa el texto que puede ser contenido de un mensaje o el body de un modal
	 * INPUT es un input que iria en el body del modal
	 * BUTTON es un boton que o va en el pie de un mensaje o en el footer del modal
	 */
	public enum ComponentType {
		TITLE, TEXT, INPUT, BUTTON
	}

	/**
	 * Los distintos tipos de Input que pueden ofrecersele al usuario para que aparezcan dentro de
	 * {@link Component}.
	 * NUMBER es un input de tipo numerico, mientras que TEXT acepta cualquier string.
	 */
	public enum InputType {
		NUMBER, TEXT
	}

	/**
	 * Acciones que puede enviar el frontend al servidor.
	 * MESSAGE se trata de un mensaje del usuario al otro usuario.
	 * ACTION es una accion a la cual el usuario hizo trigger.
	 */
	public enum ActionType {
		MESSAGE, ACTION
	}

	public sealed interface Message permits MessageResponse, ComponentResponse {}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static final class MessageResponse implements Message {
		public String message;
		public String color;
	}

	public record Action(ActionType type, String titulo, String send) {}
	public record Component(ComponentType type, String text, String name, InputType inputType, Action action){}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static final class ComponentResponse implements Message {
		public ComponentMessageType type;
		public List<Component> components;
	}


	public Type type;
	public ZonedDateTime timestamp;
	public Long from;
	public Long to;
	public Message message;
	public List<Action> availableActions;
}

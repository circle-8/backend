package org.circle8.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.chat.response.ChatMessageResponse;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMensaje {
	public Long id;
	public ChatMessageResponse.Type type;
	public ZonedDateTime timestamp;
	public Long from;
	public Long to;
	public String message;
	public Boolean ack;
	public Long recorridoId;
	public Long transaccionId;
}

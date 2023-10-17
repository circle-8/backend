package org.circle8.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.controller.chat.response.ChatMessageResponse;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mensaje {
	public Long id;
	public ChatMessageResponse.Type type;
	public ZonedDateTime timestamp;
	public Long from;
	public Long to;
	public String message;
	public boolean ack;
	public Long recorridoId;
	public Long transaccionId;
	public String chatId;
}

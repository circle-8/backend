package org.circle8.filter;

import lombok.Builder;
import org.circle8.controller.chat.response.ChatMessageResponse;

import java.time.ZonedDateTime;

@Builder
public class MensajeFilter {
	public String chatId;
	public Long recorridoId;
	public Long transaccionId;
	public ChatMessageResponse.Type type;
	public Boolean ack;
	public InequalityFilter<ZonedDateTime> timestamp;
	public Long limit;
}

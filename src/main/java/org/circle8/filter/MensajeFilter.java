package org.circle8.filter;

import lombok.Builder;
import org.circle8.controller.chat.response.ChatMessageResponse;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public class MensajeFilter {
	public List<Long> usuarios;
	public Long recorridoId;
	public Long transaccionId;
	public ChatMessageResponse.Type type;
	public Boolean ack;
	public InequalityFilter<ZonedDateTime> timestamp;
}

package org.circle8.controller.chat.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.circle8.service.chat.ExtraData;
import org.circle8.service.chat.Inputs;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
	public String type;
	public String message;
	public Map<ExtraData, Object> extraData;
	public Map<Inputs, String> inputs;
}

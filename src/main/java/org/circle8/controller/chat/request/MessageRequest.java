package org.circle8.controller.chat.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
	public String type;
	public String message;
	public Map<String, Object> extraData;
	public Map<String, Object> inputs;
}

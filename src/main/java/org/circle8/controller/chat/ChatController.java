package org.circle8.controller.chat;

import com.google.inject.Singleton;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Singleton
@Slf4j
public class ChatController implements Consumer<WsConfig> {
	private static final Map<WsContext, String> users = new ConcurrentHashMap<>();
	private int next = 1;

	@Override
	public void accept(WsConfig ws) {
		ws.onConnect(ctx -> {
			var user = "User "+next++;
			log.info("User joined");
			users.put(ctx, user);
			send("Server", user+" joined the chat");
		});
		ws.onClose(ctx -> {
			log.info("user left");
			var user = users.remove(ctx);
			send("Server", user+" left the chat");
		});
		ws.onMessage(ctx -> {
			log.info("message {}", ctx.message());
			send(users.get(ctx), ctx.message());
		});
	}

	private void send(String sender, String message) {
		users.keySet().stream()
			.filter(ctx -> ctx.session.isOpen())
			.forEach(s -> s.send(sender+" says: "+message));
	}
}

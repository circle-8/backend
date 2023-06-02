package org.circle8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;

import java.time.LocalDate;
import java.time.LocalDateTime;

class DependencyInjection extends AbstractModule {
	@Override
	protected void configure() {
		final Gson gson = new GsonBuilder()
			.registerTypeAdapter(
				LocalDateTime.class,
				(JsonSerializer<LocalDateTime>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString())
			)
			.registerTypeAdapter(
				LocalDate.class,
				(JsonSerializer<LocalDate>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString())
			)
			.create();

		super.configure();
		bind(Gson.class).toInstance(gson);
	}
}

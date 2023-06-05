package org.circle8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;

class DependencyInjection extends AbstractModule {
	@Override
	protected void configure() {
		super.configure();
		bind(Gson.class).toInstance(getGson());
		bind(DataSource.class).toInstance(getDatasource());
	}

	@NotNull
	private static Gson getGson() {
		return new GsonBuilder()
			.registerTypeAdapter(
				LocalDateTime.class,
				(JsonSerializer<LocalDateTime>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString())
			)
			.registerTypeAdapter(
				LocalDate.class,
				(JsonSerializer<LocalDate>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString())
			)
			.create();
	}

	@NotNull
	private DataSource getDatasource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://pg.germanmerkel.com.ar/test");

		config.setUsername(System.getenv("DB_USERNAME"));
		config.setPassword(System.getenv("DB_PASSWORD"));

		return new HikariDataSource(config);
	}
}

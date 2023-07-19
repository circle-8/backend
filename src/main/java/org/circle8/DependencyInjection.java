package org.circle8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class DependencyInjection extends AbstractModule {
	@Override
	protected void configure() {
		super.configure();
		bind(Gson.class).toInstance(getGson());
		bind(DataSource.class).toInstance(getDatasource());
		bind(Configuration.class).toInstance(getConfigurations());
	}

	@NotNull
	public static Gson getGson() {
		return new GsonBuilder()
			.registerTypeAdapter(
				LocalDateTime.class,
				(JsonSerializer<LocalDateTime>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
			)
			.registerTypeAdapter(
				LocalDate.class,
				(JsonSerializer<LocalDate>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.format(DateTimeFormatter.ISO_LOCAL_DATE))
			)
			.registerTypeAdapter(
				ZonedDateTime.class,
				(JsonSerializer<ZonedDateTime>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
			)
			.create();
	}

	@NotNull
	public static Configuration getConfigurations() {
		try {
			return new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class
			).configure(new Parameters()
				.properties()
				.setFileName("application.properties")
			).getConfiguration();
		} catch ( ConfigurationException e ) {
			throw new RuntimeException("Cannot load configurations", e);
		}
	}

	@NotNull
	private DataSource getDatasource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://pg.germanmerkel.com.ar/circle8");

		config.setUsername(System.getenv("DB_USERNAME"));
		config.setPassword(System.getenv("DB_PASSWORD"));

		return new HikariDataSource(config);
	}
}

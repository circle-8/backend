package org.circle8;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.circle8.route.Routes;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApiTestExtension implements BeforeAllCallback {
	private static final AtomicBoolean FIRST_TIME = new AtomicBoolean(true);

	@Singleton
	public static class Dep extends AbstractModule  {
		@Override
		protected void configure() {
			super.configure();
			bind(DataSource.class).toInstance(getDatasource());
			bind(Configuration.class).toInstance(DependencyInjection.getConfigurations());
			bind(Gson.class).toInstance(DependencyInjection.getGson());
		}

		public static DataSource getDatasource() {
			val cfg = new HikariConfig();
			cfg.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
			cfg.setConnectionTestQuery("VALUES 1");
			cfg.addDataSourceProperty("URL", "jdbc:h2:mem:public;MODE=PostgreSQL");
			cfg.addDataSourceProperty("user", "sa");
			cfg.addDataSourceProperty("password", "sa");
			cfg.setPoolName("H2 (PostgreSQL)");

			return new HikariDataSource(cfg);
		}
	}

	@Override
	public void beforeAll(ExtensionContext extensionContext) throws Exception {
		if ( FIRST_TIME.getAndSet(false) ) {
			final Injector inj = Guice.createInjector(new Dep());

			/* Start H2 database */
			org.h2.tools.Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
			var ds = inj.getInstance(DataSource.class);

			var createSchema = Objects.requireNonNull(getClass().getResourceAsStream("/sql/create_schema.sql"));
			var initSchemaSQL = CharStreams.toString(new InputStreamReader(createSchema));

			var initialData = Objects.requireNonNull(getClass().getResourceAsStream("/sql/initial_data.sql"));
			var initialDataSQL = CharStreams.toString(new InputStreamReader(initialData));

			try ( var conn = ds.getConnection() ) {
				var ps = conn.prepareStatement(initSchemaSQL);
				ps.execute();

				ps = conn.prepareStatement(initialDataSQL);
				ps.execute();
			}

			/* Start Javalin Server */
			inj.getInstance(Routes.class).initRoutes().start(8080);
		}
	}
}

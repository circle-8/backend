package org.circle8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.circle8.route.Routes;


public class Main {

	public static void main(String[] args) {
		/* Database Connection TEST */
		// String url = "jdbc:postgresql://pg.germanmerkel.com.ar/test";

		// Properties props = new Properties();
		// props.setProperty("user", System.getenv("DB_USERNAME"));
		// props.setProperty("password", System.getenv("DB_PASSWORD"));
		// props.setProperty("ssl", "false");

		// Connection conn = DriverManager.getConnection(url, props);
		// Statement st = conn.createStatement();
		// ResultSet rs = st.executeQuery("SELECT id, domain FROM click");
		// while ( rs.next() ) {
		// 	logger.info(rs.getString("id"));
		// 	logger.info(rs.getString("domain"));
		// }
		// rs.close();
		// st.close();
		// conn.close();

		// var err = new ErrorResponse();
		// err.code = ErrorCode.BAD_REQUEST;
		// err.devMessage = "hola";
		// err.message = "chau";
		// System.out.println(new Gson().toJson(err));

		final Injector inj = Guice.createInjector(new DependencyInjection());

		/* Create SERVER */
		var server = inj.getInstance(Routes.class).initRoutes().start(8080);
	}

}

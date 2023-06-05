package org.circle8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.circle8.route.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.Statement;


public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		final Injector inj = Guice.createInjector(new DependencyInjection());

		// var conn = inj.getInstance(DataSource.class).getConnection();
		// Statement st = conn.createStatement();
		// ResultSet rs = st.executeQuery("SELECT id, domain FROM click");
		// while ( rs.next() ) {
		// 	logger.info(rs.getString("id"));
		// 	logger.info(rs.getString("domain"));
		// }
		// rs.close();
		// st.close();
		// conn.close();

		/* Create SERVER */
		var server = inj.getInstance(Routes.class).initRoutes().start(8080);
	}

}

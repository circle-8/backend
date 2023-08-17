package org.circle8;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.circle8.route.Routes;


public class Main {
	public static void main(String[] args) {
		final Injector inj = Guice.createInjector(new DependencyInjection());

		/* Create SERVER */
		inj.getInstance(Routes.class).initRoutes().start(8080);
	}

}

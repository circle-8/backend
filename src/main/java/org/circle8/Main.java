package org.circle8;

import org.circle8.route.Routes;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class Main {
	public static void main(String[] args) {
		final Injector inj = Guice.createInjector(new DependencyInjection());

		/* Create SERVER */
		inj.getInstance(Routes.class).initRoutes().start(8080);
	}

}

package org.circle8.service;

import org.circle8.dao.Transaction;
import org.circle8.entity.Suscripcion;
import org.circle8.entity.User;

import com.google.inject.Singleton;

@Singleton
public class SuscripcionService {

	Suscripcion subscribe(Transaction t, User u) {
		// TODO: crear suscripcion a plan para el nuevo usuario
		// TODO: handling de suscripcion por tipos de usuario
		return new Suscripcion(1);
	}
}

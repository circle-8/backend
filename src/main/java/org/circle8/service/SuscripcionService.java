package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.dao.SuscripcionDao;
import org.circle8.dao.Transaction;
import org.circle8.entity.Plan;
import org.circle8.entity.Suscripcion;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

import java.time.LocalDate;

@Singleton
public class SuscripcionService {
	private final SuscripcionDao dao;

	@Inject public SuscripcionService(SuscripcionDao dao) { this.dao = dao; }

	Suscripcion subscribe(Transaction t, User u) throws ServiceException {
		try {
			val s = Suscripcion.builder()
				.ultimaRenovacion(LocalDate.now())
				.proximaRenovacion(LocalDate.now()) // dado que es FREE, da igual
				.plan(Plan.FREE_TRIAL)
				.build();
			return switch ( u.tipo ) {
				case ORGANIZACION -> dao.save(t, s);
				default -> new Suscripcion(0);
			};
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al crear la suscripcion al FREE TRIAL", e);
		}
	}
}

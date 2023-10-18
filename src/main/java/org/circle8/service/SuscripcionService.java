package org.circle8.service;

import java.time.LocalDate;

import org.circle8.dao.SuscripcionDao;
import org.circle8.dao.Transaction;
import org.circle8.dto.SuscripcionDto;
import org.circle8.entity.Plan;
import org.circle8.entity.Suscripcion;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.SuscripcionExpand;
import org.circle8.filter.SuscripcionFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.val;

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
	
	public SuscripcionDto get(SuscripcionFilter f, SuscripcionExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			return SuscripcionDto.from(get(t, f, x));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}
	
	private Suscripcion get(Transaction t, SuscripcionFilter f, SuscripcionExpand x) throws ServiceException {
		try {
			return this.dao.get(t, f, x)
				.orElseThrow(() -> new NotFoundException("No existe la suscripcion"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la suscripcion", e);
		}
	}
}

package org.circle8.service;

import java.time.LocalDate;
import java.util.List;

import org.circle8.dao.SuscripcionDao;
import org.circle8.dao.Transaction;
import org.circle8.dao.UserDao;
import org.circle8.dto.SuscripcionDto;
import org.circle8.entity.Plan;
import org.circle8.entity.Suscripcion;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.SuscripcionFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.val;

@Singleton
public class SuscripcionService {
	private final SuscripcionDao dao;
	private final UserDao userDao;

	@Inject
	public SuscripcionService(SuscripcionDao dao, UserDao userDao) {
		this.dao = dao;
		this.userDao = userDao;
	}

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
	
	public List<SuscripcionDto> list(SuscripcionFilter f) throws ServiceException {
		try {			
			return this.dao.list(f).stream().map(SuscripcionDto::from).toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener las suscripciones", e);
		}
	}
	
	public SuscripcionDto get(long userId) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var user = userDao.get(t, null, userId)
					.orElseThrow(() -> new NotFoundException("No existe el usuario con id " + userId));
			
			return SuscripcionDto.from(get(t, SuscripcionFilter.byId(user.suscripcion.id)));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}	
	
	
	public SuscripcionDto get(SuscripcionFilter f) throws ServiceException {
		try ( val t = dao.open(true) ) {
			return SuscripcionDto.from(get(t, f));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}
	
	private Suscripcion get(Transaction t, SuscripcionFilter f) throws ServiceException {
		try {
			return this.dao.get(t, f)
				.orElseThrow(() -> new NotFoundException("No existe la suscripcion"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la suscripcion", e);
		}
	}
}

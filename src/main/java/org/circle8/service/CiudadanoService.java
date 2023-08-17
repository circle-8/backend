package org.circle8.service;

import com.google.inject.Inject;
import org.circle8.dao.CiudadanoDao;
import org.circle8.dao.Transaction;
import org.circle8.entity.Ciudadano;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

public class CiudadanoService {
	private final CiudadanoDao dao;

	@Inject
	public CiudadanoService(CiudadanoDao dao) {
		this.dao = dao;
	}

	Ciudadano save(Transaction t, User u) throws ServiceException {
		var c = new Ciudadano(u.id);
		try {
			c = dao.save(t, c);
			u.ciudadanoId = c.id;
			return c;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el ciudadano", e);
		}
	}
}

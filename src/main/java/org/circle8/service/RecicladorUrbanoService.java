package org.circle8.service;

import com.google.inject.Inject;
import org.circle8.dao.RecicladorUrbanoDao;
import org.circle8.dao.Transaction;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

public class RecicladorUrbanoService {

	private final RecicladorUrbanoDao dao;

	@Inject
	public RecicladorUrbanoService(RecicladorUrbanoDao dao) {
		this.dao = dao;
	}

	User save(Transaction t, User u) throws ServiceException {
		// TODO: esto deberia devolver un RecicladorUrbano :(
		try {
			u.recicladorUrbanoId = dao.save(t, u);
			return u;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el reciclador", e);
		}
	}
	
	void update(Transaction t, User u) throws NotFoundException, ServiceError {
		try {
			dao.update(t, u);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el reciclador", e);
		}
	}
}

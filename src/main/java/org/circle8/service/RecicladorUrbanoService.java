package org.circle8.service;

import com.google.inject.Inject;
import org.circle8.dao.RecicladorUrbanoDao;
import org.circle8.dao.Transaction;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

public class RecicladorUrbanoService {

	private final RecicladorUrbanoDao dao;

	@Inject
	public RecicladorUrbanoService(RecicladorUrbanoDao dao) {
		this.dao = dao;
	}

	void save(Transaction t, User u) throws ServiceException {
		try {
			var id = dao.save(t, u);
			u.id = id;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el reciclador", e);
		}
	}
}

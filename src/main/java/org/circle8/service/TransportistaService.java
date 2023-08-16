package org.circle8.service;

import org.circle8.dao.Transaction;
import org.circle8.dao.TransportistaDao;
import org.circle8.entity.Ciudadano;
import org.circle8.entity.Transportista;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

import com.google.inject.Inject;

public class TransportistaService {
	private final TransportistaDao dao;

	@Inject
	public TransportistaService(TransportistaDao dao) {
		this.dao = dao;
	}

	Transportista save(Transaction t, Ciudadano c) throws ServiceException {
		var tr = new Transportista(c.usuarioId);
		try {
			return dao.save(t, tr);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el transportista", e);
		}
	}
}

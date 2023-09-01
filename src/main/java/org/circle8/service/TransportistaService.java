package org.circle8.service;

import java.util.Optional;

import org.circle8.dao.Transaction;
import org.circle8.dao.TransportistaDao;
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

	Transportista save(Transaction t, Long userId) throws ServiceException {
		var tr = Transportista.builder().usuarioId(userId).build();
		try {
			return dao.save(t, tr);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el transportista", e);
		}
	}
	
	Optional<Transportista> getByUsuarioId(Transaction t, Long userId) throws PersistenceException {
		return this.dao.get(t, null, userId);
	}
}

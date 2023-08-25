package org.circle8.service;

import com.google.inject.Inject;
import org.circle8.dao.OrganizacionDao;
import org.circle8.dao.Transaction;
import org.circle8.entity.Organizacion;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

public class OrganizacionService {
	private final OrganizacionDao dao;

	@Inject public OrganizacionService(OrganizacionDao dao) { this.dao = dao; }

	Organizacion save(Transaction t, Organizacion o) throws ServiceException {
		try {
			return dao.save(t, o);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar la organizacion", e);
		}
	}
}

package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoVerdeDao;
import org.circle8.dto.PuntoVerdeDto;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.filter.PuntoVerdeFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PuntoVerdeService {

	private final PuntoVerdeDao dao;

	@Inject
	public PuntoVerdeService(PuntoVerdeDao dao) {
		this.dao = dao;
	}

	/**
	 * Obtiene el listado de puntos verdes
	 */
	public List<PuntoVerdeDto> list(PuntoVerdeFilter filter) throws ServiceError{
		try {
			return this.dao.list(filter).stream().map(PuntoVerdeDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos verdes", e);
		}
	}
}

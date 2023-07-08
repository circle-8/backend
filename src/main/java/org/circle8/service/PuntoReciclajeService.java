package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoReciclajeDao;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.filter.PuntoReciclajeFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PuntoReciclajeService {

	private final PuntoReciclajeDao dao;
	
	@Inject
	public PuntoReciclajeService(PuntoReciclajeDao dao) {
		this.dao = dao;
	}
	
	/**
	 * Obtiene el listado de puntos de reciclaje
	 * @return
	 * @throws ServiceError
	 */
	public List<PuntoReciclajeDto> list(PuntoReciclajeFilter filter) throws ServiceError{
		try {
			return this.dao.getPuntos(filter).stream().map(PuntoReciclajeDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos de reciclaje", e);
		}
	}
}

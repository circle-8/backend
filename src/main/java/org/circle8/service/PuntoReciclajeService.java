package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoReciclajeDao;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.exception.NotFoundException;
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
	 */
	public List<PuntoReciclajeDto> list(PuntoReciclajeFilter filter) throws ServiceError{
		try {
			return this.dao.list(filter).stream().map(PuntoReciclajeDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos de reciclaje", e);
		}
	}

	/**
	 * Obtiene un punto de reciclaje por medio de su id
	 */
	public PuntoReciclajeDto get(Long id, Long recicladorId) throws ServiceException {
		try {
			return PuntoReciclajeDto.from(this.dao.get(id, recicladorId).
				orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de puntos de reciclaje", e);
		}
	}
}

package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoReciclajeDao;
import org.circle8.dto.PuntoReciclajeDto;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
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

	public PuntoReciclajeDto save(PuntoReciclajeDto dto) throws ServiceError {
		var entity = dto.toEntity();
		try(var t = dao.open()) {
			entity = dao.save(t, entity);
			dto.id = entity.id;

			//TODO: llamar al otro dao
			t.commit();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el punto de reciclaje", e);
		}
		return dto;
	}
}
